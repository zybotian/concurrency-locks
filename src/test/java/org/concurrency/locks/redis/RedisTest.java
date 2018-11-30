package org.concurrency.locks.redis;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author tianbo
 * @date 2018-11-30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() throws Exception {
        BoundListOperations<String, String> testList = redisTemplate.boundListOps("_paul_test_list_" + System.nanoTime());

        for (int i = 1; i <= 100; i++) {
            testList.rightPush(i + "");
        }

        List<String> range = testList.range(0, 100);
        Assert.assertEquals(100, range.size());
        for (int i = 1; i <= 100; i++) {
            Assert.assertEquals(i + "", range.get(i - 1));
        }
    }
}
