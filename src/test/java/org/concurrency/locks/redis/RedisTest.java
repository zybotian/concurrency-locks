package org.concurrency.locks.redis;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tianbo
 * @date 2018-11-30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Ignore
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() throws Exception {
        String key = "_paul_test_list_" + System.nanoTime();
        BoundListOperations<String, String> testList = redisTemplate.boundListOps(key);

        for (int i = 1; i <= 100; i++) {
            testList.rightPush(i + "");
        }

        List<String> range = testList.range(0, 100);
        Assert.assertEquals(100, range.size());
        for (int i = 1; i <= 100; i++) {
            Assert.assertEquals(i + "", range.get(i - 1));
        }
        redisTemplate.delete(key);
    }

    @Test
    public void testLua() throws Exception {

        String lua = "local stockConfig = 'activity_purchase_stock_config_hashmap_'..KEYS[1]\n" +
                "local stockConfigKey = 'activity_purchase_stock_config_stock_key_'..KEYS[1]\n" +
                "local userPurchaseProductList = 'activity_purchase_user_product_list_'..KEYS[1]\n" +
                "local stock = redis.call('hget', stockConfig, stockConfigKey)\n" +
                "if stock == nil then return -1 end\n" +
                "local stockNumber = tonumber(stock)\n" +
                "if stockNumber <= 0 then\n" +
                "local finishMark = redis.call('setnx','activity_purchase_status_'..KEYS[1],1)" +
                "return tonumber(finishMark) end\n" +
                "redis.call('hset', stockConfig, stockConfigKey, stockNumber-1)\n" +
                "redis.call('rpush', userPurchaseProductList, ARGV[1]..'-'..ARGV[2]..'-'..ARGV[3])\n" +
                "return 2\n";

        List<String> keys = new ArrayList<>();
        keys.add("201812031015");

        String argv1 = "2";
        String argv2 = "2018112815220001";
        String argv3 = "purchase_v4";

        RedisScript<Long> luaScript = new DefaultRedisScript<>(lua, Long.class);
        Long result = redisTemplate.execute(luaScript, keys, argv1, argv2, argv3);

        String argv21 = "4";
        String argv22 = "2018112815220001";
        String argv23 = "purchase_v4";
        Long result2 = redisTemplate.execute(luaScript, keys, argv21, argv22, argv23);

        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        Object stockInRedis = hashOps.get(Config.PURCHASE_STOCK_MAP + Config.PURCHASE_KEY, Config
                .PURCHASE_STOCK_KEY + Config.PURCHASE_KEY);

        int stock = NumberUtils.toInt(stockInRedis.toString());

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String statusInRedis = valueOps.get(Config.PURCHASE_STATUS + Config.PURCHASE_KEY);

        int status = NumberUtils.toInt(statusInRedis);

        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> range = listOps.range(Config.PURCHASE_USER_PRODUCT_LIST + Config.PURCHASE_KEY, 0, -1);
    }

}
