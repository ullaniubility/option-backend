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
public enum TableNameEnums {

    BTCUSDT("ba_quotation_btcusdt"),

    MATICUSDT("ba_quotation_maticusdt");

    private final String tableName;

}
