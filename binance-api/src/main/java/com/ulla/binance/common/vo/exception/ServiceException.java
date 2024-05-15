package com.ulla.binance.common.vo.exception;

import com.ulla.binance.common.enums.ResultCodeEnums;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 全局业务异常类
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 3447728300174142127L;

    public static final String DEFAULT_MESSAGE = "网络错误，请稍后重试！";

    /**
     * 异常消息
     */
    private String msg = DEFAULT_MESSAGE;

    /**
     * 错误码
     */
    private ResultCodeEnums resultCodeEnums;

    public ServiceException(String msg) {
        this.resultCodeEnums = ResultCodeEnums.ERROR;
        this.msg = msg;
    }

    public ServiceException() {
        super();
    }

    public ServiceException(ResultCodeEnums resultCodeEnums) {
        this.resultCodeEnums = resultCodeEnums;
    }

    public ServiceException(ResultCodeEnums resultCodeEnums, String message) {
        this.resultCodeEnums = resultCodeEnums;
        this.msg = message;
    }

}
