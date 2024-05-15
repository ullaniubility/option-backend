package com.ulla.modules.payment.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 入金订单
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-02-27 18:14:50
 */
@Data
@TableName("money_payment_transaction")
public class MoneyPaymentTransactionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @TableId
    private Long id;
    /**
     * 交易订单Id(法币购买第三方支付订单Id，数字币生成订单Id)
     */
    private String orderId;
    /**
     * 0：待付款 1：已付款 2：已完成 3：已失效 4：已失败
     */
    private Integer orderStatus;
    /**
     * 会员账号
     */
    private String mail;
    /**
     * 用户openId
     */
    private String openId;
    /**
     * 用户主键ID - 对应用户表中的uid
     */
    private Long uid;
    /**
     * 推荐人
     */
    private Long inviteId;
    /**
     * 页面选择的按钮id-校验页面选择的入金金额和配置的按钮金额是否一致
     */
    private String buttonCode;
    /**
     * 入金金额 - 页面选择的入金金额
     */
    private BigDecimal estimatedDepositAmount;

    /**
     * 奖励配置Id
     */
    private String rewardCode;
    /**
     * 奖励金额
     */
    private BigDecimal rewardAmount;
    /**
     * 促销优惠金额
     */
    private BigDecimal preferentialAmount;
    /**
     * 入金金额- 应付金额 - 调用时传递给第三方支付的金额
     */
    private BigDecimal actualPaymentAmount;
    /**
     * 公司实际到账金额
     */
    private BigDecimal actualReceivedAmount;
    /**
     * 第三方支付平台实际支付金额
     */
    private BigDecimal channelPaymentAmount;
    /**
     * 支付渠道名称
     */
    private String channelName;
    /**
     * 支付渠道类型: 0：法币购买 - 实际到账数字货币 1：法币购买-实际到账法币 2：数字货币购买 - 实际到账数字货币
     */
    private Integer channelType;
    /**
     * 币链(实际到账数字币有值)
     */
    private String net;
    /**
     * 币种符号(实际到账数字币有值)
     */
    private String symbol;
    /**
     * 到账币种数量- 实际到账数字币有值
     */
    private BigDecimal currencyAmount;
    /**
     * 到账币价格-交易时价格 -实际到账数字币有值
     */
    private BigDecimal currencyPrice;
    /**
     * 是否首次入金 0-首充 1-不是首充
     */
    private Integer isFirstOrder;
    /**
     * 是否使用促销代码 0：未使用 1：使用
     */
    private Integer isUsePreferential;
    /**
     * 优惠券id
     */
    private Long couponId;
    /**
     * 促销优惠券码
     */
    private String preferentialCode;
    /**
     * 收币地址
     */
    private String address;
    /**
     * 收币地址(小写)
     */
    private String addressLower;
    /**
     * 代币合约地址
     */
    private String contractAddress;
    /**
     * 数字币交易hash
     */
    private String transactionHash;
    /**
     * 订单状态修改时间
     */
    private Long updateTime;
    /**
     * 订单创建时间
     */
    private Long createTime;

    /**
     * 入金 - 货币单位
     */
    private String depositMonetaryUnit;
    /**
     * 第三方支付平台实际支付的-货币单位
     */
    private String channelMonetaryUnit;

    @TableField(exist = false)
    private String wallectAddress;

    /**
     * 订单消息通知是否已推送 0.创建订单 1.待推送消息 2. 已推送消息
     */
    private Integer ifRead = 0;

    /**
     * @Description 说明
     * @author zhuyongdong
     * @since 2023-05-23 15:37:13
     */
    private String remark;
}
