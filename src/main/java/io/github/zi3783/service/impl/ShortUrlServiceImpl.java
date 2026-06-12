package io.github.zi3783.service.impl;

import cn.hutool.core.lang.Snowflake;
import io.github.zi3783.entity.ShortUrlMap;
import io.github.zi3783.mapper.ShortUrlMapper;
import io.github.zi3783.service.ShortUrlService;
import io.github.zi3783.util.ShortUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    @Autowired
    private ShortUrlMapper shortUrlMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private ShortUrlGenerator shortUrlGenerator;

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

        //查询数据库，如果已经存在，那么直接返回
        Map<String,Object> map = new HashMap();
        map.put("is_deleted",0);
        map.put("target_url",targetUrl);
        List<ShortUrlMap> shortUrls = shortUrlMapper.selectByMap(map);
        if(shortUrls != null){
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

        //返回短链
        return encode;
    }
}
