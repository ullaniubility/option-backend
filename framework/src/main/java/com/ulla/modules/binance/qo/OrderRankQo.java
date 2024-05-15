package com.ulla.modules.binance.qo;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 订单盈利排名
 * @since 2023/2/24 17:39
 */
@Data
public class OrderRankQo extends WebsocketMessageBase {

    /**
     * 交易对
     */
    String symbol;

    /**
     * 0关 1开
     */
    Integer isOpen;

}
