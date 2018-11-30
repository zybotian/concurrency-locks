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

    /**
     * 可重入乐观锁的最大尝试次数
     */
    private static final int REENTRANTLOCK_RETRY_TIMES = 3;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ServiceProxy serviceProxy;

    /**
     * 使用悲观锁进行并发控制
     * 存在的问题: 性能
     * @param userId
     * @param productId
     * @param source
     * @return
     * @throws BusinessException
     */
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
            log.warn("update stock failed, user id:{}, product id:{}", userId, productId);
            throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
        }

        log.info("update stock success, user id:{}, product id:{}, version:{}", userId, productId, product.getVersion());

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
     * 存在的问题: 与悲观锁相比,请求成功率降低了
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
            log.warn("update stock failed, user id:{}, product id:{}, version:{}", userId, productId, product.getVersion());
            throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
        }

        log.info("update stock success, user id:{}, product id:{}, version:{}", userId, productId, product.getVersion());

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

    /**
     * 使用乐观锁进行并发控制,存在请求成功率低的问题
     * 解决方法: 通过重试机制来提高请求成功率
     * (1) 通过重试次数来限制请求量
     * (2) 通过重试时间来限制请求量
     * 这里使用次数来限制, (2)的实现与之类似
     * @param userId
     * @param productId
     * @param source
     * @return
     * @throws BusinessException
     */
    @Transactional(rollbackFor = BusinessException.class)
    public boolean purchaseV3(Integer userId, Long productId, String source) throws BusinessException {
        int retryTimes = 0;
        do {
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
                // 修改失败,计数器加1,继续下一次尝试
                log.warn("update stock failed, user id:{}, product id:{}, retry time:{}", userId, productId, retryTimes);
                retryTimes++;
                continue;
            }
            log.info("update stock success, user id:{}, product id:{}, retry time:{}", userId, productId, retryTimes);
            // 修改成功,插入购买记录
            OrderItem orderItem = createOrderItem(userId, productId, source);
            // 插入购买记录, 若插入失败时,事务回滚(库存扣减成功但购买记录未保存成功)
            int insertOrderItemResult = orderItemDao.insert(orderItem);
            if (insertOrderItemResult < 0) {
                log.warn("insert order item failed, user id:{}, product id:{}", userId, productId);
                throw new BusinessException(ErrorCode.DB_SERVICE_ERROR);
            }

            // 库存扣减成功,且购买记录插入成功
            return true;
        } while (retryTimes < REENTRANTLOCK_RETRY_TIMES); // 尝试3次,增大请求成功的概率
        return false;
    }
}
