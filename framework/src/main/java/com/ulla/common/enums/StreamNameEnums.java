package com.ulla.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhuyongdong
 * @Description websocket事件枚举
 * @since 2023/2/25 16:25
 */
@Getter
@AllArgsConstructor
public enum StreamNameEnums {

    OPEN_QUOTATION(0, "openQuotation"),

    HIS_QUOTATION(1, "hisQuotation"),

    SYMBOL_LIST(2, "symbolList"),

    OPEN_KLINE_QUOTATION(3, "openKlineQuotation"),

    HIS_KLINE_QUOTATION(4, "hisKlineQuotation"),

    SETTLEMENT_COUNTDOWN(5, "settlementCountdown"),

    ORDER_RESULT(6, "orderResult"),

    ORDER_RANK(7, "orderRank");

    private final Integer columnType;

    private final String desc;

}
