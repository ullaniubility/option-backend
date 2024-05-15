package com.ulla.common.vo;

import java.io.Serializable;

import com.ulla.common.enums.ResultCodeEnums;
import lombok.Data;

/**
 * @Description 前后端交互VO
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */

@Data
public class ResultMessageVo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success;

    /**
     * 消息
     */
    private String message;

    /**
     * 返回代码
     */
    private Integer code;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 结果对象
     */
    private T result;

    /**
     * 返回成功默认状态码200
     */
    public static <T> ResultMessageVo<T> ok() {
        return returnResult(true, ResultCodeEnums.SUCCESS.message(), ResultCodeEnums.SUCCESS.code(), null);
    }

    /**
     * 返回成功默认状态码200
     *
     * @Param data 返回的数据
     */
    public static <T> ResultMessageVo<T> ok(T data) {
        return returnResult(true, ResultCodeEnums.SUCCESS.message(), ResultCodeEnums.SUCCESS.code(), data);
    }

    /**
     * 返回成功 自定义状态码消息
     *
     * @Param msg  返回的消息
     */
    public static <T> ResultMessageVo<T> ok(String msg) {
        return returnResult(true, msg, ResultCodeEnums.SUCCESS.code(), null);
    }

    /**
     * 返回成功 自定义状态码消息
     *
     * @Param data 返回的数据
     * @Param code 返回的状态码
     * @Param msg  返回的消息
     */
    public static <T> ResultMessageVo<T> ok(String msg, T data) {
        return returnResult(true, msg, ResultCodeEnums.SUCCESS.code(), data);
    }

    /**
     * 返回成功 自定义状态码消息
     *
     * @Param data 返回的数据
     * @Param code 返回的状态码
     * @Param msg  返回的消息
     */
    public static <T> ResultMessageVo<T> ok(String msg, int code, T data) {
        return returnResult(true, msg, code, data);
    }

    /**
     * 操作失败
     */
    public static <T> ResultMessageVo<T> fail() {
        return returnResult(false, ResultCodeEnums.ERROR.message(), ResultCodeEnums.ERROR.code(), null);
    }

    /**
     * 失败返回结果
     *
     * @param msg 返回失败的消息
     */
    public static <T> ResultMessageVo<T> fail(String msg) {
        return returnResult(false, msg, ResultCodeEnums.ERROR.code(), null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> ResultMessageVo<T> fail(ResultCodeEnums errorCode) {
        return returnResult(false, errorCode.message(), errorCode.code(), null);
    }

    /**
     * 返回失败
     *
     * @Param msg 返回失败的消息 必须
     * @Param code 返回失败的错误码 必须
     */
    public static <T> ResultMessageVo<T> fail(String msg, int code) {
        return returnResult(false, msg, code, null);
    }

    /**
     * 返回失败
     *
     * @Param data 返回的数据
     * @Param code 返回的状态码
     * @Param msg  返回的消息
     */
    public static <T> ResultMessageVo<T> fail(String msg, int code, T data) {
        return returnResult(false, msg, code, data);
    }

    public static <T> ResultMessageVo<T> returnResult(boolean success, String msg, int code, T data) {
        ResultMessageVo<T> rspData = new ResultMessageVo<>();
        rspData.setSuccess(success);
        rspData.setCode(code);
        rspData.setMessage(msg);
        rspData.setResult(data);
        return rspData;
    }
}
