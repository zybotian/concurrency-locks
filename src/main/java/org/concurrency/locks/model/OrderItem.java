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
public class OrderItem {
    // 订单id
    private long id;
    // 用户id
    private String userId;
    // 产品id
    private long productId;
    // 产品数量
    private int number;
    // 请求来源
    private String source;
    // 记录创建时间
    private long createTime;
    // 记录修改时间
    private long updateTime;
}
