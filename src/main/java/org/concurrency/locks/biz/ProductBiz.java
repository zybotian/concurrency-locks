package org.concurrency.locks.biz;

import org.concurrency.locks.dao.OrderItemDao;
import org.concurrency.locks.dao.ProductDao;
import org.concurrency.locks.exception.BusinessException;
import org.concurrency.locks.exception.ErrorCode;
import org.concurrency.locks.model.OrderItem;
import org.concurrency.locks.model.Product;
import org.concurrency.locks.proxy.ServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tianbo
 * @date 2018-11-28
 */
@Service
@Slf4j
public class ProductBiz {

    /**
     * 前12位数字表示user id, 后面为系统生成的唯一id
     */
    private static final String FORMAT = "%012d%s";

    @Resource(name = "orderItemDao")
    private OrderItemDao orderItemDao;

    @Resource(name = "productDao")
    private ProductDao productDao;

    @Autowired
    private ServiceProxy serviceProxy;

    @Transactional(rollbackFor = BusinessException.class)
    public boolean purchaseV1(int userId, long productId, String source) throws BusinessException {
        // 获取记录锁
        Product product = productDao.lockById(productId);
        if (product == null) {
            log.info("get product lock failed, user id:{}, product id:{}", userId, productId);
            return false;
        }
        // 检查库存
        if (product.getStock() <= 0) {
            log.warn("product stock is zero, operation is terminated, user id:{}, product id:{}", userId, productId);
            return false;
        }
        // 库存扣减操作, 若扣减失败时,事务回滚
        int updateStock = productDao.update(productId, product.getStock() - 1, serviceProxy.getTimeStamp());
        if (updateStock <= 0) {
            log.warn("updateV2 stock failed, user id:{}, product id:{}", userId, productId);
            throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
        }

        OrderItem orderItem = createOrderItem(userId, productId, source);
        // 插入购买记录, 若插入失败时,事务回滚
        int insertOrderItemResult = orderItemDao.insert(orderItem);
        if (insertOrderItemResult < 0) {
            log.warn("insert order item failed, user id:{}, product id:{}", userId, productId);
            throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
        }
        // 购买成功
        return true;
    }

    private OrderItem createOrderItem(int userId, long productId, String source) {
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

    /**
     * 使用乐观锁进行并发控制
     * @param userId
     * @param productId
     * @param source
     * @return
     * @throws BusinessException
     */
    @Transactional(rollbackFor = BusinessException.class)
    public boolean purchaseV2(Integer userId, Long productId, String source) throws BusinessException {
        // 查询,不加锁
        Product product = productDao.findById(productId);
        if (product == null) {
            log.info("get product failed, user id:{}, product id:{}", userId, productId);
            return false;
        }
        // 检查库存
        if (product.getStock() <= 0) {
            log.warn("product stock is zero, operation is terminated, user id:{}, product id:{}", userId, productId);
            return false;
        }
        // 库存扣减操作,注意版本号加1,所有的修改操作均需要执行版本号加1操作,若扣减失败,事务回滚
        int updateStock = productDao.updateV2(productId, product.getStock() - 1, product.getVersion() + 1,
                product.getVersion(), serviceProxy.getTimeStamp());
        if (updateStock <= 0) {
            log.warn("updateV2 stock failed, user id:{}, product id:{}, version:{}", userId, productId, product.getVersion());
            throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
        }

        OrderItem orderItem = createOrderItem(userId, productId, source);
        // 插入购买记录, 若插入失败时,事务回滚
        int insertOrderItemResult = orderItemDao.insert(orderItem);
        if (insertOrderItemResult < 0) {
            log.warn("insert order item failed, user id:{}, product id:{}", userId, productId);
            throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
        }
        // 购买成功
        return true;
    }
}
