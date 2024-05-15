package com.ulla.rocketmq.exception;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public class JSONConvertException extends RuntimeException {

    public JSONConvertException() {

    }

    public JSONConvertException(String message) {
        super(message);
    }

    public JSONConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONConvertException(Throwable cause) {
        super(cause);
    }

    public JSONConvertException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
