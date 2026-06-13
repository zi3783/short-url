package io.github.zi3783.util;

import io.github.zi3783.mapper.ShortUrlMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BloomFilterLoader implements CommandLineRunner {

    @Autowired
    private RBloomFilter<String> shortUrlBloomFilter;

    @Autowired
    private ShortUrlMapper shortUrlMapper;

    @Override
    public void run(String... args) throws Exception {
        List<Long> list = shortUrlMapper.getIdAndTargetUrl();
        for(Long id : list){
            shortUrlBloomFilter.add(String.valueOf(id));
        }
        log.info("布隆过滤器加载完毕");
    }
}
