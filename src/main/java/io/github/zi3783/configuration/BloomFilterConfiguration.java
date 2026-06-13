package io.github.zi3783.configuration;


import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BloomFilterConfiguration {

    @Bean
    public RBloomFilter<String> shortUrlBloomFilter(RedissonClient redissonClient){
        RBloomFilter<String> shortUrlBloomFilter = redissonClient.getBloomFilter("shortUrlBloomFilter");
        if(shortUrlBloomFilter.isExists()){
            log.info("BloomFilter already exists");
        }else{
            log.info("BloomFilter created");
            shortUrlBloomFilter.tryInit(100000L, 0.01);
        }
        return shortUrlBloomFilter;
    }

}
