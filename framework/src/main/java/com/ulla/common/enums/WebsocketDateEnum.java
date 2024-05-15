package com.ulla.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * websocket数据返回类型枚举
 */
@Getter
@AllArgsConstructor
public enum WebsocketDateEnum {

    ACTUAL(0, "行情实时数据"),

    INITIAL(1, "行情初始历史数据"),

    HIS(2, "行情历史数据"),

    K_ACTUAL(3, "K线实时数据"),

    K_INITIAL(4, "K线初始历史数据"),

    K_HIS(5, "K线历史数据"),

    TRANSACTION_CATEGORY(6, "交易一级大类列表"),

    TRANSACTION_CATEGORY_CHILD(7, "交易二级类列表"),

    ORDER_CALCULATION(8, "订单倒计时"),

    ORDER_RESULT(9, "用户部分订单回显结果"),

    QUOTATION_ORDER(10, "用户当前生效中订单行情组"),

    ORDER_RANK_BY_SYMBOL(11, "交易对排名"),

    ORDER_RANK(12, "每分钟所有订单排名"),

    CHARGE_MSG(13, "充值消息");

    private final Integer columnType;

    private final String desc;

}
