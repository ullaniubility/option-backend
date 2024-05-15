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
public enum QueryKLineEnums {

    FIVE_SECONDS(1, "5s"), TEN_SECONDS(2, "10s"), FIFTEEN_SECONDS(3, "15s"), THIRTY_SECONDS(4, "30s"),
    ONE_MINUTE(5, "1m"), FIVE_MINUTE(6, "5m"), FIFTEEN_MINUTE(7, "15m"), THIRTY_MINUTE(8, "30m"), ONE_HOUR(9, "1h"),
    FOUR_HOUR(10, "4h"), ONE_DAY(11, "1d"), SEVEN_DAY(12, "7d"), ONE_MONTH(13, "1mon");

    private final Integer code;
    private final String value;

    /**
     * 通过Code判断枚举值是否存在
     */
    public static boolean isExist(Integer code) {
        if (code == null) {
            return false;
        }
        for (QueryKLineEnums e : values()) {
            if (code.intValue() == e.code.intValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过Code判断枚举值是否存在
     */
    public static boolean isExist(String value) {
        if (value == null) {
            return false;
        }
        for (QueryKLineEnums e : values()) {
            if (value.equals(e.getValue())) {
                return true;
            }
        }
        return false;
    }

}
