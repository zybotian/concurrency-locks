# concurrency-locks

示例项目以**限时抢购**为背景,总结了3种常用的并发控制策策略.

设计的表结构为:

- 产品表
```sql
CREATE TABLE IF NOT EXISTS `db_conc_lock`.`product`(
  `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '自增主键',
  `type`            INT             NOT NULL DEFAULT 0       COMMENT '类型',
  `name`            VARCHAR(32)     NOT NULL DEFAULT ''      COMMENT '名称',
  `stock`           INT             NOT NULL DEFAULT 0       COMMENT '库存数量',
  `price`           INT             NOT NULL DEFAULT 0       COMMENT '单价',
  `version`         BIGINT          NOT NULL DEFAULT 0       COMMENT '版本号',
  `create_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '创建时间',
  `update_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '更新时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
```

- 购买记录表
```sql
CREATE TABLE IF NOT EXISTS `db_conc_lock`.`order_item`(
  `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '自增主键',
  `user_id`         VARCHAR(64)     NOT NULL DEFAULT ''      COMMENT '用户id',
  `product_id`      BIGINT          NOT NULL DEFAULT 0       COMMENT '产品id',
  `number`          INT             NOT NULL DEFAULT 0       COMMENT '购买数量',
  `source`          VARCHAR(16)     NOT NULL DEFAULT ''      COMMENT '请求来源',
  `create_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '创建时间',
  `update_time`     BIGINT          NOT NULL DEFAULT 0       COMMENT '更新时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
```

#### 1. 悲观锁
   1. 查询产品
   - **select** where `id` = ? **for update**
   2. 检查库存
   - if (product.getStock()>0)
   3. 修改库存
   - **update** `product` **set `stock` = stock-1** where `id` = ?
   4. 插入购买记录
   - insert into `order_item`
#### 2. 乐观锁
   1. 查询产品
   - **select** where `id` = ?
   2. 检查库存
   - if (product.getStock()>0)
   3. 带版本号修改库存
   - **update** `product` **set `stock` = stock-1, `version` = version+1** where `id` = ? and `version` = version
   - 如果修改成功,认为获取锁成功,否则获取锁失败,用户未抢购到,抢购流程结束
   4. 插入购买记录
   - insert into `order_item`

#### 3 可重入乐观锁
使用乐观锁之后,获取到锁的概率会大大降低,可以使用基于时间的可重入乐观锁或者基于次数的可重入乐观锁.
##### 3.1 基于时间的可重入乐观锁
```java
public class Lock {
    public boolean tryLock() {
        long startTime = System.currentTimeMillis();
        do {
            // 乐观锁实现方式
            if (拿到锁) {
                return true;
            }
        } while (System.currentTimeMillis() - startTime < 100); // 100ms内可再次尝试
    }
}
```
##### 3.2 基于次数的可重入乐观锁
```java
public class Lock {
    public boolean tryLock() {
        int retryTimes = 0;
        do {
            // 乐观锁实现方式
            if (拿到锁) {
                return true;
            }
            retryTimes++;
        } while (retryTimes < 3); // 可尝试3次
    }
}
```
#### 4. Redis + Lua脚本
利用Lua脚本的原子性,将库存查询,库存检查,库存扣减以及记录购买信息都放在Lua中实现,待抢购结束,再将redis数据同步到mysql.
```java
    String lua = "local stockConfig = 'activity_purchase_stock_config_hashmap_'..KEYS[1]\n" +
                "local stockConfigKey = 'activity_purchase_stock_config_stock_key_'..KEYS[1]\n" +
                "local userPurchaseProductList = 'activity_purchase_user_product_list_'..KEYS[1]\n" +
                "local stock = redis.call('hget', stockConfig, stockConfigKey)\n" +
                "if stock == nil then return -1 end\n" +
                "local stockNumber = tonumber(stock)\n" +
                "if stockNumber <= 0 then\n" +
                "local finishFlag = redis.call('setnx','activity_purchase_status_'..KEYS[1],1)" +
                "return tonumber(finishFlag) end\n" +
                "redis.call('hset', stockConfig, stockConfigKey, stockNumber-1)\n" +
                "redis.call('rpush', userPurchaseProductList, ARGV[1]..'-'..ARGV[2]..'-'..ARGV[3])\n" +
                "return 2\n";
    RedisScript<Long> luaScript = new DefaultRedisScript<>(lua, Long.class);
    Long result = redisTemplate.execute(luaScript, keys, argv1, argv2, argv3);
```
每次抢购之后,检查执行结果,如果是库存为0,执行数据同步
```java
public class Biz {
    public task(){
        if (purchaseResult == PurchaseResult.STOCK_ZERO) {
            // 触发异步任务执行同步操作,使用setnx保证只执行一次
            syncPurchaseStatusTask.checkAndSync();
        }
    }
}
```
