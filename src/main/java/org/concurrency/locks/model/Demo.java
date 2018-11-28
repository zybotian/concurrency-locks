package org.concurrency.locks.model;

import lombok.*;

/**
 * @author tianbo
 * @date 2018-11-27
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demo {
    private long id;
    private int type;
    private String content;
    private long createTime;
    private long updateTime;
}
