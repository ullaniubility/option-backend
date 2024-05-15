package com.ulla.common.utils;

import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.vo.ResultMessageVo;

/**
 * @Description 返回结果工具类
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
public class ResultUtil<T> {

    /**
     * 抽象类，存放结果
     */
    private final ResultMessageVo<T> resultMessage;
    /**
     * 正常响应
     */
    private static final Integer SUCCESS = 200;

    /**
     * 构造话方法，给响应结果默认值
     */
    public ResultUtil() {
        resultMessage = new ResultMessageVo<>();
        resultMessage.setSuccess(true);
        resultMessage.setMessage("success");
        resultMessage.setCode(SUCCESS);
    }

    /**
     * 返回数据
     *
     * @param t
     *            范型
     * @return 消息
     */
    public ResultMessageVo<T> setData(T t) {
        this.resultMessage.setResult(t);
        return this.resultMessage;
    }

    /**
     * 返回成功消息
     *
     * @param resultCodeEnums
     *            返回码
     * @return 返回成功消息
     */
    public ResultMessageVo<T> setSuccessMsg(ResultCodeEnums resultCodeEnums) {
        this.resultMessage.setSuccess(true);
        this.resultMessage.setMessage(resultCodeEnums.message());
        this.resultMessage.setCode(resultCodeEnums.code());
        return this.resultMessage;

    }

    /**
     * 服务器异常 追加状态码
     *
     * @param code
     *            状态码
     * @param msg
     *            返回消息
     * @return 消息
     */
    public ResultMessageVo<T> setSuccessMsg(Integer code, String msg) {
        this.resultMessage.setSuccess(true);
        this.resultMessage.setMessage(msg);
        this.resultMessage.setCode(code);
        return this.resultMessage;
    }

    /**
     * 抽象静态方法，返回结果集
     * 
     * @param t
     *            范型
     * @param <T>
     *            范型
     * @return 消息
     */
    public static <T> ResultMessageVo<T> data(T t) {
        return new ResultUtil<T>().setData(t);
    }

    /**
     * 返回成功
     *
     * @param resultCodeEnums
     *            返回状态码
     * @return 消息
     */
    public static <T> ResultMessageVo<T> success(ResultCodeEnums resultCodeEnums) {
        return new ResultUtil<T>().setSuccessMsg(resultCodeEnums);
    }

    /**
     * 返回成功
     * 
     * @return 消息
     */
    public static <T> ResultMessageVo<T> success() {
        return new ResultUtil<T>().setSuccessMsg(ResultCodeEnums.SUCCESS);
    }

    /**
     * 返回成功
     *
     * @param code
     *            状态码
     * @param msg
     *            返回消息
     * @return 消息
     */
    public static <T> ResultMessageVo<T> success(Integer code, String msg) {
        return new ResultUtil<T>().setSuccessMsg(code, msg);
    }

    /**
     * 返回失败
     *
     * @param resultCodeEnums
     *            返回状态码
     * @return 消息
     */
    public static <T> ResultMessageVo<T> error(ResultCodeEnums resultCodeEnums) {
        return new ResultUtil<T>().setErrorMsg(resultCodeEnums);
    }

    /**
     * 返回失败
     *
     * @param code
     *            状态码
     * @param msg
     *            返回消息
     * @return 消息
     */
    public static <T> ResultMessageVo<T> error(Integer code, String msg) {
        return new ResultUtil<T>().setErrorMsg(code, msg);
    }

    /**
     * 服务器异常 追加状态码
     * 
     * @param resultCodeEnums
     *            返回码
     * @return 消息
     */
    public ResultMessageVo<T> setErrorMsg(ResultCodeEnums resultCodeEnums) {
        this.resultMessage.setSuccess(false);
        this.resultMessage.setMessage(resultCodeEnums.message());
        this.resultMessage.setCode(resultCodeEnums.code());
        return this.resultMessage;
    }

    /**
     * 服务器异常 追加状态码
     *
     * @param code
     *            状态码
     * @param msg
     *            返回消息
     * @return 消息
     */
    public ResultMessageVo<T> setErrorMsg(Integer code, String msg) {
        this.resultMessage.setSuccess(false);
        this.resultMessage.setMessage(msg);
        this.resultMessage.setCode(code);
        return this.resultMessage;
    }

}
