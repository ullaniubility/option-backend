package com.ulla.binance.common.constant;

/**
 * @author zhuyongdong
 * @Description 币安公共常量
 * @since 2023/2/27 11:10
 */
public class CommonConstant {

    /**
     * 缓存分隔符
     */
    public final static String SPLIT = ":";
    /**
     * 缓存公共前缀
     */
    public final static String PREFIX = "binance:api" + SPLIT;

    /**
     * 交易对list
     */
    public final static String SYMBOL_LIST = PREFIX + "symbolList";

}
