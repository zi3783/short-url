package io.github.zi3783.service.impl;

import cn.hutool.core.lang.Snowflake;
import io.github.zi3783.entity.ShortUrlMap;
import io.github.zi3783.exception.BusinessException;
import io.github.zi3783.mapper.ShortUrlMapper;
import io.github.zi3783.service.ShortUrlService;
import io.github.zi3783.util.FreshMysql;
import io.github.zi3783.util.ShortUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final String REDIS_KEY_PREFIX = "id:";
    private static final String REDIS_LOCK_PREFIX = "lock:";

    @Autowired
    private ShortUrlMapper shortUrlMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ShortUrlGenerator shortUrlGenerator;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private FreshMysql freshMySQL;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RBloomFilter rBloomFilter;

    /**
     * 创建短链
     * @param targetUrl
     * @return
     */
    @Override
    public String createShortUrl(String targetUrl){
        if(targetUrl==null || targetUrl.isEmpty()){
            throw new IllegalArgumentException("参数错误");
        }
        //有效性
        if(!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")){
            targetUrl = "http://" + targetUrl;
        }

        //查询数据库，如果已经存在，那么直接返回
        //加锁
        RLock lock = redissonClient.getLock(REDIS_LOCK_PREFIX + Math.abs(targetUrl.hashCode()));
        lock.lock(10, TimeUnit.SECONDS);
        try {
            Map<String,Object> map = new HashMap();
            map.put("is_deleted",0);
            map.put("target_url",targetUrl);
            List<ShortUrlMap> shortUrls = shortUrlMapper.selectByMap(map);
            if(!shortUrls.isEmpty()){
                //已存在
                log.info("数据库中已经存在短链路");
                return shortUrls.get(0).getShortUrl();
            }
            //生成短链
            long id = snowflake.nextId();
            String encode = shortUrlGenerator.encode(id);

            //插入mysql
            ShortUrlMap entity = ShortUrlMap.builder()
                    .id(id)
                    .shortUrl(encode)
                    .targetUrl(targetUrl)
                    .isDeleted(0)
                    .build();
            shortUrlMapper.insert(entity);

            //写入布隆过滤器
            rBloomFilter.add(String.valueOf(id));
            //写入redis缓存
            redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + id, targetUrl, 30, TimeUnit.MINUTES);

            //返回短链
            return encode;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 解析短链
     * @param shortUrl
     * @return
     */
    @Override
    public String getTargetUrl(String shortUrl) {
        if(shortUrl==null || shortUrl.isEmpty()){
            throw new IllegalArgumentException("参数错误");
        }
        //解析短链获得id
        long id = shortUrlGenerator.decode(shortUrl);

        //查找redis缓存找数据
        String key = REDIS_KEY_PREFIX + id;
        String target = (String) redisTemplate.opsForValue().get(key);

        //在redis找到了返回
        if(target != null){
            freshMySQL.increment(id);
            return target;
        }

        //布隆过滤器防止缓存穿透
        boolean contains = rBloomFilter.contains(String.valueOf(id));
        if(!contains){
            log.info("布隆过滤器中不存在");
            throw new BusinessException("没找到目标地址");
        }

        //没找到查找mysql
        //加锁防止缓存击穿
        String lockKey = REDIS_LOCK_PREFIX + id;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(10, TimeUnit.SECONDS);
        try{
            //再次查redis
            target = (String) redisTemplate.opsForValue().get(key);
            if(target != null){
                freshMySQL.increment(id);
                return target;
            }
            //查找数据库
            ShortUrlMap targetUrl = shortUrlMapper.selectById(id);
            if(targetUrl==null){
                throw new BusinessException("没找到目标地址");
            }
            if(targetUrl.getIsDeleted()==1){
                throw new BusinessException("目标地址已删除");
            }
            if(targetUrl.getExpireTime() != null &&
                    targetUrl.getExpireTime().getTime() <= System.currentTimeMillis()) {
                throw new BusinessException("目标地址已过期");
            }
            //写入redis
            try{
                redisTemplate.opsForValue().set(key, targetUrl.getTargetUrl(), 30, TimeUnit.MINUTES);
            }catch (Exception e){
                //写入redis失败
                log.warn("redis写入失败");
            }
            log.info("访问成功");

            freshMySQL.increment(id);
            return targetUrl.getTargetUrl();
        }finally {
            //释放锁
            lock.unlock();
        }
    }
}
