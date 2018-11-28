package org.concurrency.locks.dao;

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
public class DemoDaoTest {

    @Autowired
    private DemoDao demoDao;

    @Test
    public void testInsert() throws Exception {
        Demo demo = Demo.builder().type(1).content("content").build();
        int insert = demoDao.insert(demo);
        Assert.assertTrue(insert > 0);
    }
}
