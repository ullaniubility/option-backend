package com.ulla.common.vo.exception;

import lombok.Getter;

/**
 * @Description RSAE异常类
 * @author zhuyongdong
 * @since 2023-01-04 10:24:56
 */
@Getter
public class RSAException extends RuntimeException {

    private final String message;

    public RSAException(String message) {
        this.message = message;
    }

}
