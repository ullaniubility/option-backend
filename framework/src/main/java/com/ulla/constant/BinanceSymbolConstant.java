package com.ulla.constant;

/**
 * @author zhuyongdong
 * @Description 行情推送缓存管理常量
 * @since 2023/2/27 21:13
 */
public class BinanceSymbolConstant {

    /**
     * 缓存分隔符
     */
    public final static String SPLIT = ":";
    /**
     * 缓存公共前缀
     */
    public final static String QUOTATION_PREFIX = "openQuotation" + SPLIT;

    public final static String KLINE_PREFIX = "openKlineQuotation" + SPLIT;

    public final static String KLINE_PREFIX_5S = KLINE_PREFIX + "5s" + SPLIT;
    public final static String KLINE_PREFIX_10S = KLINE_PREFIX + "10s" + SPLIT;
    public final static String KLINE_PREFIX_15S = KLINE_PREFIX + "15s" + SPLIT;
    public final static String KLINE_PREFIX_30S = KLINE_PREFIX + "30s" + SPLIT;
    public final static String KLINE_PREFIX_1M = KLINE_PREFIX + "1m" + SPLIT;
    public final static String KLINE_PREFIX_5M = KLINE_PREFIX + "5m" + SPLIT;
    public final static String KLINE_PREFIX_15M = KLINE_PREFIX + "15m" + SPLIT;
    public final static String KLINE_PREFIX_30M = KLINE_PREFIX + "30m" + SPLIT;
    public final static String KLINE_PREFIX_1H = KLINE_PREFIX + "1h" + SPLIT;
    public final static String KLINE_PREFIX_4H = KLINE_PREFIX + "4h" + SPLIT;
    public final static String KLINE_PREFIX_1D = KLINE_PREFIX + "1d" + SPLIT;
    public final static String KLINE_PREFIX_7D = KLINE_PREFIX + "7d" + SPLIT;

    public static class BTC_USDT {
        public final static String QUOTATION_BTCUSDT = QUOTATION_PREFIX + "BTCUSDT" + SPLIT;
        public final static String KLINE_BTCUSDT = KLINE_PREFIX + "BTCUSDT" + SPLIT;
        public final static String BTCUSDT_5S = KLINE_BTCUSDT + "5s" + SPLIT;
        public final static String BTCUSDT_10S = KLINE_BTCUSDT + "10s" + SPLIT;
        public final static String BTCUSDT_15S = KLINE_BTCUSDT + "15s" + SPLIT;
        public final static String BTCUSDT_30S = KLINE_BTCUSDT + "30s" + SPLIT;
        public final static String BTCUSDT_1M = KLINE_BTCUSDT + "1m" + SPLIT;
    }

    public static class MATIC_USDT {
        public final static String QUOTATION_MATICUSDT = QUOTATION_PREFIX + "MATICUSDT" + SPLIT;
        public final static String KLINE_MATICUSDT = KLINE_PREFIX + "MATICUSDT" + SPLIT;
        public final static String MATICUSDT_5S = KLINE_MATICUSDT + "5s" + SPLIT;
        public final static String MATICUSDT_10S = KLINE_MATICUSDT + "10s" + SPLIT;
        public final static String MATICUSDT_15S = KLINE_MATICUSDT + "15s" + SPLIT;
        public final static String MATICUSDT_30S = KLINE_MATICUSDT + "30s" + SPLIT;
        public final static String MATICUSDT_1M = KLINE_MATICUSDT + "1m" + SPLIT;
    }

}
