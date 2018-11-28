package org.concurrency.locks.biz;

import org.concurrency.locks.dao.DemoDao;
import org.concurrency.locks.model.Demo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author tianbo
 * @date 2018-11-27
 */
@Service
public class DemoBiz {

    @Resource(name = "demoDao")
    private DemoDao demoDao;

    public int insert(Demo demo) {
        return demoDao.insert(demo);
    }

    public Demo findNewestCreated() {
        return demoDao.findNewestCreated();
    }
}
