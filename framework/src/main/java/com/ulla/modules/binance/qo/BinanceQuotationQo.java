package com.ulla.modules.binance.qo;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/24 17:39
 */
@Data
public class BinanceQuotationQo extends WebsocketMessageBase {

    /**
     * 币安行情K线交易对
     */
    String symbol;

    /**
     * 币安行情查询开始时间
     */
    Long startDataTime;
    /**
     * 币安行情查询结束时间
     */
    Long endDataTime;

    /**
     * K线类型 5s 10s 15s 30s 1m 5m 15m 30m 1h 4h 1d 7d 1mon
     */
    String klineType;

    /**
     * 请求编号
     */
    String requestUuid;
}
