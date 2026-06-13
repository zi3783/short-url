package io.github.zi3783;

import io.github.zi3783.util.FreshMysql;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FreshMysqlTest {
    @Autowired
    private FreshMysql freshMySQL;
    @Test
    public void testFreshCounter() {
        freshMySQL.increment(2065376647137333248L);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
