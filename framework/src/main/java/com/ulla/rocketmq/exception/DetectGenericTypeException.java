package com.ulla.rocketmq.exception;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public class DetectGenericTypeException extends Exception {

    public DetectGenericTypeException(String message) {
        super(message);
    }

    public DetectGenericTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
