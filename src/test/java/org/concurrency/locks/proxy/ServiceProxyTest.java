package org.concurrency.locks.proxy;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author tianbo
 * @date 2018-11-28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Ignore
public class ServiceProxyTest {

    @Autowired
    ServiceProxy serviceProxy;

    @Test
    public void testGetId() throws Exception {
        int number = 1000000;
        Set<String> set = new HashSet<>(number);
        for (int i = 0; i < number; i++) {
            set.add(serviceProxy.getId());
        }
        Assert.assertEquals(number, set.size());
    }
}
