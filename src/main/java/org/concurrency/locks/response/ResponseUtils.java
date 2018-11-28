package org.concurrency.locks.response;

import com.alibaba.fastjson.JSON;

import org.concurrency.locks.exception.ErrorCode;

/**
 * @author tianbo
 * @date 2018-11-28
 */

public class ResponseUtils {

    public static String getSuccess() {
        return getByErrorCode(ErrorCode.SUCCESS);
    }

    public static String getFailed() {
        return getByErrorCode(ErrorCode.REQUEST_FAILED_ERROR);
    }

    public static String getByErrorCode(ErrorCode errorCode) {
        return JSON.toJSONString(new JsonResult(errorCode));
    }

    public static <T> String getSuccess(T data) {
        return getByErrorCode(data, ErrorCode.SUCCESS);
    }

    public static <T> String getFailed(T data) {
        return getByErrorCode(data, ErrorCode.REQUEST_FAILED_ERROR);
    }

    public static <T> String getByErrorCode(T data, ErrorCode errorCode) {
        return JSON.toJSONString(new JsonResult(data, errorCode));
    }
}
