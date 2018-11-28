package org.concurrency.locks.proxy;

/**
 * @author tianbo
 * @date 2018-11-28
 */
public interface ServiceProxy {
    /**
     * 获取唯一id
     * @return
     */
    String getId();

    /**
     * 获取系统时间
     */
    long getTimeStamp();
}
