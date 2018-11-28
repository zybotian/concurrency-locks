package org.concurrency.locks.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tianbo
 * @date 2018-11-16 Friday 15:12
 */
@Data
@AllArgsConstructor
public class BusinessException extends Exception {

    private ErrorCode errorCode;

    @Override
    public String toString() {
        return "BusinessException{" +
                "errorCode=" + errorCode +
                '}';
    }
}
