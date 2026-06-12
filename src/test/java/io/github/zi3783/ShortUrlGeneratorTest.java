package io.github.zi3783;

import io.github.zi3783.util.ShortUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ShortUrlGeneratorTest {

//    @Test
    public void shortUrlGeneratorTest() {
        // 直接创建对象，不需要 @Autowired
        ShortUrlGenerator shortUrlGenerator = new ShortUrlGenerator();

        long id = 1000863;

        // 编码
        String encode = shortUrlGenerator.encode(id);
        log.info("编码: {} → {}", id, encode);

        // 解码
        long decode = shortUrlGenerator.decode(encode);
        log.info("解码: {} → {}", encode, decode);

        // 断言
        assertEquals(id, decode);

        log.info("测试通过！");
    }
}