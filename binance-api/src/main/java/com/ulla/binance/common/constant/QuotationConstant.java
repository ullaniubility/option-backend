package com.ulla.binance.common.constant;

/**
 * @author zhuyongdong
 * @Description 币安行情缓存字段常量
 * @since 2023/2/27 11:10
 */
public class QuotationConstant {

    /**
     * 缓存分隔符
     */
    public final static String SPLIT = ":";
    /**
     * 缓存公共前缀
     */
    public final static String PREFIX = "binanceServe:quotation" + SPLIT;
    /**
     * 结算区编号
     */
    public final static String TRADING_RANGE_ID = PREFIX + "tradingRangeId" + SPLIT;
    /**
     * 交易区编号
     */
    public final static String ORDER_RANGE_ID = PREFIX + "orderRangeId" + SPLIT;

    /**
     * 币安优化后行情1S存储
     */
    public final static String BINANCE_QUOTATION_DATA_1S = PREFIX + "data" + SPLIT + "1S" + SPLIT;
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
     * 行情数据组1S
     */
    public final static String BINANCE_QUOTATION_CACHE_1S = "binance:quotation:cache" + SPLIT + "1S" + SPLIT;
    /**
     * 行情数据组5S
     */
    public final static String BINANCE_QUOTATION_CACHE_5S = "binance:quotation:cache" + SPLIT + "5S" + SPLIT;
    /**
     * 行情数据组10S
     */
    public final static String BINANCE_QUOTATION_CACHE_10S = "binance:quotation:cache" + SPLIT + "10S" + SPLIT;
    /**
     * 行情数据组15S
     */
    public final static String BINANCE_QUOTATION_CACHE_15S = "binance:quotation:cache" + SPLIT + "15S" + SPLIT;
    /**
     * 行情数据组30S
     */
    public final static String BINANCE_QUOTATION_CACHE_30S = "binance:quotation:cache" + SPLIT + "30S" + SPLIT;
    /**
     * 行情数据组1M
     */
    public final static String BINANCE_QUOTATION_CACHE_1M = "binance:quotation:cache" + SPLIT + "1M" + SPLIT;

    /**
     * @Description 资管报价查询
     * @author zhuyongdong
     * @since 2023-04-04 10:26:41
     */
    public final static String BINANCE_QUOTATION_ARBUSDT = "asset:quotation" + SPLIT;;

}
