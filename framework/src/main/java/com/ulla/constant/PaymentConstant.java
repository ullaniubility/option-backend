package com.ulla.constant;

/**
 * 常量
 *
 * @author michael
 */
public class PaymentConstant {

    // 第三方支付渠道展示属性：0首次入金展示 1非首次入金展示 2不管是不是首次入金全部展示
    public static final String ZERO = "0";// 首次入金展示
    public static final String ONE = "1";// 非首次入金展示
    public static final String TWO = "2";// 全部展示

    // 支付来源归属 - 0钱包支付 1电商支付 2期权充值
    public static final Integer DELIVER_TYPE_TWO = 2;// 期权充值

    // 入金订单 - 订单状态
    public static final Integer outstanding_payment = 0;// 待付款
    public static final Integer completed_payment = 1;// 已付款
    public static final Integer accomplish = 2;// 已完成
    public static final Integer lose_efficacy = 3;// 已失效
    public static final Integer error = 4;// 已失败
    public static final Integer completed_payment_pre = 5;// 已付款,未到扫链

    // 入金 - 法币第三方支付渠道返回的订单状态
    public static final Integer PRE_CREATED = 100;// 未到第三方支付渠道-先临时创建订单
    public static final Integer CREATED = 110;// 创建订单
    public static final Integer WAITFORPAYING = 120;// 等待支付
    public static final Integer PENDING = 130;// 支付中
    public static final Integer WAIFFORTRANSACTION = 140;// 渠道订单完结并成功，等待我们自己转账给用户
    public static final Integer VIRTUALCURRENCYTRANSFERFAILED = 145;// 渠道订单完结并成功（收到用户法币转账），虚拟币转账失败
    public static final Integer FAILED = 150;// 失败
    public static final Integer CANCELED = 160;// 取消
    public static final Integer REFUNDED = 170;// 退款
    public static final Integer SUCCESS = 199;// 全部成功-包含我们自己的转账成功

    // erc20的usdt合约
    public static final String ERC20_USDT_CONTRACT = "0xdac17f958d2ee523a2206206994597c13d831ec7";
    // trc20的usdt合约
    public static final String TRC20_USDT_CONTRACT = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";

    // 平台常用的支持货币单位
    public static final String USD = "USD";// 美元

}
