package com.ulla.binance.common.vo;

import java.io.Serializable;

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
}
