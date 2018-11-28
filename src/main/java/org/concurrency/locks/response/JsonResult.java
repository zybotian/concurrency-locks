package org.concurrency.locks.response;

import org.concurrency.locks.exception.ErrorCode;

import lombok.*;

/**
 * @author tianbo
 * @date 2018-11-28
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private T data;

    public JsonResult(ErrorCode errorCode) {
        this.success = errorCode == ErrorCode.SUCCESS;
        this.code = errorCode.getCode();
        this.message = errorCode.getDesc();
    }

    public JsonResult(T data) {
        this(data, ErrorCode.SUCCESS);
    }

    public JsonResult(T data, ErrorCode errorCode) {
        this(errorCode);
        this.data = data;
    }
}
