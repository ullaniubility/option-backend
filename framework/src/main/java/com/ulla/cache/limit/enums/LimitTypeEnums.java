package com.ulla.cache.limit.enums;

/**
 * @Description 限流类型
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
public enum LimitTypeEnums {
    /**
     * 自定义key(即全局限流)
     */
    CUSTOMER,
    /**
     * 根据请求者IP（IP限流）
     */
    IP
}
