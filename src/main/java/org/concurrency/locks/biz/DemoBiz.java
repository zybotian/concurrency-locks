package org.concurrency.locks.biz;

import org.concurrency.locks.dao.DemoDao;
import org.concurrency.locks.model.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author tianbo
 * @date 2018-11-27
 */
@Service
public class DemoBiz {

    @Autowired
    private DemoDao demoDao;

    public int insert(Demo demo) {
        return demoDao.insert(demo);
    }

    public Demo findNewestCreated() {
        return demoDao.findNewestCreated();
    }
}
