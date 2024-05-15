package com.ulla.constant;

public class RocketMqConstants {
    /**
     * 注册
     */
    public static final String REGISTER_PUT = "register_put";// 注册

    public static final String REGISTER_QUEUE_PUT = "register_queue_put";

    public static final String REGISTER_ROUTINGKEY_PUT = "register_routingKey_put";

    /**
     * 订单
     */
    public static final String ORDER_PUT = "order_put";// 下单
    public static final String ORDER_PUT_TAG = "order_put_tag";// 下单

    /**
     * 盈亏更改
     */
    public static final String PROFITLOSS_PRICE_PUT = "profitLoss_price_put";// 资产

    public static final String PROFITLOSS_PRICE_QUEUE_PUT = "profitLoss_price_queue_put";

    public static final String PROFITLOSS_PRICE_ROUTINGKEY_PUT = "profitLoss_price_routingKey_put";

    /**
     * 钱包余额回调
     */
    public static final String WALLET_PUT = "wallet_put";// 注册

    public static final String WALLET_QUEUE_PUT = "wallet_queue_put";

    public static final String WALLET_ROUTINGKEY_PUT = "wallet_routingKey_put";
    /**
     * 钱包转账回调
     */
    public static final String PAYMENTS_PUT = "payments_put";// 注册

    public static final String PAYMENTS_QUEUE_PUT = "payments_queue_put";

    public static final String PAYMENT_ROUTINGKEY_PUT = "payments_routingKey_put";

    /**
     * 币安行情
     */
    public static final String BINANCE_QUOTATION_5M = "payments_put";// 5mK线

    /**
     * 充值信息推送*
     */
    public static final String CHARGE_MSG = "charge_msg";

}
