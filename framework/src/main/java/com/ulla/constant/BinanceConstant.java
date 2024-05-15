package com.ulla.constant;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author zhuyongdong
 * @Description 币安行情常量
 * @since 2023/2/27 21:13
 */
public class BinanceConstant {

    /**
     * 缓存分隔符
     */
    public final static String SPLIT = ":";
    /**
     * 缓存公共前缀
     */
    public final static String PREFIX = "binance:quotation" + SPLIT;

    /**
     * 行情数据组1S
     */
    public final static String BINANCE_QUOTATION_CACHE_1S = PREFIX + "cache" + SPLIT + "1S" + SPLIT;
    /**
     * 币安优化后行情5S存储
     */
    public final static String BINANCE_QUOTATION_DATA_5S = PREFIX + "data" + SPLIT + "5S" + SPLIT;
    /**
     * 币安优化后行情5S统计
     */
    public final static String BINANCE_QUOTATION_TEMP_5S = PREFIX + "temp" + SPLIT + "5S" + SPLIT;
    /**
     * 币安优化后行情10S统计
     */
    public final static String BINANCE_QUOTATION_TEMP_10S = PREFIX + "temp" + SPLIT + "10S" + SPLIT;
    /**
     * 币安优化后行情15S统计
     */
    public final static String BINANCE_QUOTATION_TEMP_15S = PREFIX + "temp" + SPLIT + "15S" + SPLIT;
    /**
     * 币安优化后行情30S统计
     */
    public final static String BINANCE_QUOTATION_TEMP_30S = PREFIX + "temp" + SPLIT + "30S" + SPLIT;
    /**
     * 币安优化后行情1M统计
     */
    public final static String BINANCE_QUOTATION_TEMP_1M = PREFIX + "temp" + SPLIT + "1M" + SPLIT;
    /**
     * 行情数据组5S
     */
    public final static String BINANCE_QUOTATION_CACHE_5S = PREFIX + "cache" + SPLIT + "5S" + SPLIT;
    /**
     * 行情数据组10S
     */
    public final static String BINANCE_QUOTATION_CACHE_10S = PREFIX + "cache" + SPLIT + "10S" + SPLIT;
    /**
     * 行情数据组15S
     */
    public final static String BINANCE_QUOTATION_CACHE_15S = PREFIX + "cache" + SPLIT + "15S" + SPLIT;
    /**
     * 行情数据组30S
     */
    public final static String BINANCE_QUOTATION_CACHE_30S = PREFIX + "cache" + SPLIT + "30S" + SPLIT;
    /**
     * 行情数据组1M
     */
    public final static String BINANCE_QUOTATION_CACHE_1M = PREFIX + "cache" + SPLIT + "1M" + SPLIT;
    /**
     * 订单行情组
     */
    public final static String BINANCE_QUOTATION_ORDER = PREFIX + "order" + SPLIT;

    /**
     * 行情趋势
     */
    public final static String BINANCE_QUOTATION_TREND = PREFIX + "trend" + SPLIT;

    public final static List<String> ADDRESS_NET = Lists.newArrayList("ETH", "BTC", "TRX", "BSC", "HECO", "MATIC");
}
