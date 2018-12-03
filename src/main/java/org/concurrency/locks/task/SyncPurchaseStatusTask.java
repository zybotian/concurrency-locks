package org.concurrency.locks.task;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.concurrency.locks.biz.OrderItemBiz;
import org.concurrency.locks.biz.ProductBiz;
import org.concurrency.locks.exception.BusinessException;
import org.concurrency.locks.exception.ErrorCode;
import org.concurrency.locks.model.OrderItem;
import org.concurrency.locks.proxy.ServiceProxy;
import org.concurrency.locks.redis.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tianbo
 * @date 2018-12-03
 */
@Slf4j
@Service
public class SyncPurchaseStatusTask {

    @Autowired
    private ProductBiz productBiz;

    @Autowired
    private OrderItemBiz orderItemBiz;

    @Autowired
    private ServiceProxy serviceProxy;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Async
    @Transactional(rollbackFor = BusinessException.class)
    public void checkAndSync() throws BusinessException {
        // 检查redis中的库存剩余
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        Object stockInRedis = hashOps.get(Config.PURCHASE_STOCK_MAP + Config.PURCHASE_KEY, Config
                .PURCHASE_STOCK_KEY + Config.PURCHASE_KEY);

        int stock = NumberUtils.toInt(stockInRedis.toString());
        if (stock > 0) {
            log.warn("stock in redis is not zero:{}", stockInRedis);
            return;
        }

        // 检查redis中的结束标记
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String statusInRedis = valueOps.get(Config.PURCHASE_STATUS + Config.PURCHASE_KEY);

        int finished = NumberUtils.toInt(statusInRedis);
        if (finished != 1) {
            log.warn("finish flag in redis is not SUCC:{}", statusInRedis);
            return;
        }

        // 获取购买产品的用户列表
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        List<String> userProducts = listOps.range(Config.PURCHASE_USER_PRODUCT_LIST + Config.PURCHASE_KEY, 0, -1);
        if (CollectionUtils.isEmpty(userProducts)) {
            log.warn("user purchase list is empty:{}", userProducts);
            return;
        }
        log.info("user purchase list:{}", userProducts);

        Map<Long, Integer> productIdCountMap = new HashMap<>();
        for (String userProduct : userProducts) {
            String[] tokens = StringUtils.split(userProduct, "-");
            if (tokens == null || tokens.length < 3) {
                log.warn("invalid user product token:{}", userProduct);
                continue;
            }
            int userId = NumberUtils.toInt(tokens[0]);
            long productId = NumberUtils.toLong(tokens[1]);
            String source = tokens[2];
            OrderItem orderItem = orderItemBiz.createOrderItem(userId, productId, source);
            int insert = orderItemBiz.insert(orderItem);
            if (insert <= 0) {
                throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
            }
            log.info("insert user purchase records success, product id:{}, user id:{}", productId, userId);
            Integer count = productIdCountMap.get(productId);
            if (count == null) {
                count = 1;
            } else {
                count += 1;
            }
            productIdCountMap.put(productId, count);
        }

        for (Map.Entry<Long, Integer> entry : productIdCountMap.entrySet()) {
            int updateStock = productBiz.decreaseStock(entry.getKey(), entry.getValue(), serviceProxy.getTimeStamp());
            if (updateStock <= 0) {
                throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
            }
            log.info("update stock success, product id:{}, decrease stock amount:{}", entry.getKey(), entry.getValue());
        }
    }
}
