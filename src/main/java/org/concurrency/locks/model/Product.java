package org.concurrency.locks.model;

import lombok.*;

/**
 * @author tianbo
 * @date 2018-11-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    // 产品id
    private long id;
    // 产品类型
    private int type;
    // 产品名字
    private String name;
    // 产品库存
    private int stock;
    // 产品价格
    private int price;
    // 版本号
    private long version;
    // 创建时间
    private long createTime;
    // 修改时间
    private long updateTime;
}
