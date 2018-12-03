package org.concurrency.locks.biz;

import org.concurrency.locks.dao.OrderItemDao;
import org.concurrency.locks.model.OrderItem;
import org.concurrency.locks.proxy.ServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tianbo
 * @date 2018-12-03
 */
@Service
public class OrderItemBiz {
    /**
     * 前12位数字表示user id, 后面为系统生成的唯一id
     */
    private static final String FORMAT = "%012d%s";

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ServiceProxy serviceProxy;

    public int insert(OrderItem orderItem) {
        return orderItemDao.insert(orderItem);
    }

    public OrderItem createOrderItem(int userId, long productId, String source) {
        return OrderItem.builder()
                .userId(createInternalRequestId(userId))
                .productId(productId)
                .number(1)
                .source(source)
                .createTime(serviceProxy.getTimeStamp())
                .updateTime(serviceProxy.getTimeStamp())
                .build();
    }

    private String createInternalRequestId(int originId) {
        return String.format(FORMAT, originId, serviceProxy.getId());
    }
}
