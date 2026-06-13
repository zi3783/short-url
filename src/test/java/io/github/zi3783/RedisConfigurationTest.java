package io.github.zi3783;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@SpringBootTest
public class RedisConfigurationTest {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void redisConfigurationTest() {
        redisTemplate.opsForValue().set("key", "value");

        Object key =(String) redisTemplate.opsForValue().get("key");
        log.info("key:{}", key);
    }

}
