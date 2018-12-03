package org.concurrency.locks.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tianbo
 * @date 2018-12-03
 */
@Getter
@AllArgsConstructor
public enum PurchaseResult {

    STOCK_NOT_ENOUGH(0, "库存不足"),
    STOCK_ZERO(1, "库存为0"),
    SUCCESS(2, "购买成功"),
    // 添加其他
    ;

    private static final Map<Integer, PurchaseResult> codeMap = new HashMap<>();

    static {
        for (PurchaseResult purchaseResult : PurchaseResult.values()) {
            codeMap.put(purchaseResult.code, purchaseResult);
        }
    }

    private int code;
    private String desc;

    public static PurchaseResult findByCode(int code) {
        return codeMap.get(code);
    }
}
