package org.concurrency.locks.proxy;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author tianbo
 * @date 2018-11-28
 */

@Service
public class ServiceProxyImpl implements ServiceProxy {

    @Override
    public String getId() {
        return UUID.randomUUID().toString().replaceAll("-", "") + System.nanoTime();
    }

    @Override
    public long getTimeStamp() {
        return System.currentTimeMillis();
    }
}
