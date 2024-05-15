package com.ulla.modules.binance.qo;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 用户正在倒计时的订单
 * @since 2023/2/24 17:39
 */
@Data
public class SettleQo extends WebsocketMessageBase {

    /**
     * 用户token
     */
    String token;

    String requestUuid;

    /**
     * 订单编号
     */
    String ids;

    /**
     * 交易对
     */
    String symbol;

}
