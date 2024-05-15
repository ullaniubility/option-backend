package com.ulla.binance.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhuyongdong
 * @Description websocket事件枚举
 * @since 2023/2/25 16:25
 */
@Getter
@AllArgsConstructor
public enum QuotationKLineEnums {

    ONE_SECONDS(1, "1s"), FIVE_MINUTE(2, "5m"), FIFTEEN_MINUTE(3, "15m"), THIRTY_MINUTE(4, "30m"), ONE_HOUR(5, "1h"),
    FOUR_HOUR(6, "4h"), ONE_DAY(7, "1d"), ONE_WEEK(8, "1w"), ONE_MONTH(9, "1M");

    private final Integer code;
    private final String value;

}
