package com.ulla.rocketmq.exception;

import org.springframework.beans.BeansException;

/**
 * @author zhuyongdong
 * @Description
 * @since 2023/3/7 14:55
 */
public class NoMethodParameterException extends BeansException {

    public NoMethodParameterException(String message) {
        super(message);
    }

}
