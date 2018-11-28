package org.concurrency.locks.biz;

import org.concurrency.locks.model.Demo;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author tianbo
 * @date 2018-11-27
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-test.xml")
@Ignore
public class DemoBizTest {

    @Autowired
    private DemoBiz demoBiz;

    @Test
    public void testInsert() throws Exception {
        int insert = demoBiz.insert(Demo.builder().type(10).content("content").build());
        Assert.assertTrue(insert > 0);
    }
}
