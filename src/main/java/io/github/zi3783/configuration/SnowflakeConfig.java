package io.github.zi3783.configuration;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SnowflakeConfig {
    @Bean
    public Snowflake snowflake() {
        long ipLong = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
        long workerId = ipLong % 32;
        long datacenterId = 1;
        log.info("雪花算法初始化：workerId:{},datacenterId:{}", workerId, datacenterId);
        return new Snowflake(workerId, datacenterId);
    }
}
