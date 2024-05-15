package com.ulla.modules.payment.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建法币支付订单时需要的参数VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalCurrencyParamerVO {

    /**
     * 0：待付款
     1：已付款
     2：已完成
     3：已失效
     4：已失败
     */
    private Integer orderStatus;
    /**
     * 会员账号
     */
    private String mail;
    /**
     * 用户主键ID- 对应用户表中的uid
     */
    private Long userId;
    /**
     * 会员UID - 对应用户表中的openId
     */
    private String uid;
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
     * 是否首次入金 0:不是首次入金 1:是首次入金
     */
    private Integer isFirstOrder;
    /**
     * 是否使用促销代码 0：未使用 1：使用
     */
    private Integer isUsePreferential;
    /**
     * 促销代码
     */
    private String preferentialCode;

    /**
     * 促销id
     */
    private String preferentialId;
    /**
     * 收币地址
     */
    private String address;
    /**
     * 代币合约地址
     */
    private String contractAddress;



    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "订单副id")
    private String subOrderId;

    @NotBlank(message = "渠道唯一的标识必填!")
    @ApiModelProperty(value = "渠道标识-渠道唯一的标识")
    private String channelIdent;

    @ApiModelProperty(value = "订单类型 买或卖  buy sell")
    private String type;

    @ApiModelProperty(value = "金额")
    private BigDecimal value;

    @ApiModelProperty(value = "输入框的金额")
    private BigDecimal inputValue;

    @ApiModelProperty(value = "金额种类-USD等")
    private String currency;

//    @ApiModelProperty(value = "到账地址")
//    private String toAddress;

    @ApiModelProperty(value = "到账金额")
    private String tokenAmount;

//    @ApiModelProperty(value = "合约地址-代币的智能合约地址-会变动")
//    private String contract;

//    @ApiModelProperty(value = "创建人-可以为空")
//    private String userId;

    @ApiModelProperty(value = "合作方id")
    private String businessId;

    @ApiModelProperty(value = "合作方储存的该用户的id")
    private String businessUserId;

    @ApiModelProperty(value = "渠道方储存的该用户的id")
    private String channelUserId;

    @ApiModelProperty(value = "创建订单的时间")
    private Date createdTime;

    @ApiModelProperty(value = "换算价格，即虚拟币的实时价格")
    private BigDecimal conversionPrice;

    @ApiModelProperty(value = "支付来源归属 - 0钱包支付 1电商支付 2期权充值")
    private Integer deliverGoods;

    @ApiModelProperty(value = "主题色")
    private String themeColorVal;

    @ApiModelProperty(value = "语言")
    private String localeId;

    @ApiModelProperty(value = "订单状态变更后回调URL")
    private String callUrl;

    @ApiModelProperty(value = "订单支付后(仅支付，并不是完成)跳转的URL")
    private String reUrl;

    @ApiModelProperty(value = "用户邮箱")
    private String userEmail;

    /**
     * 入金 - 货币单位
     */
    private String depositMonetaryUnit;
    /**
     * 第三方支付平台实际支付的-货币单位
     */
    private String channelMonetaryUnit;

    /**
     * 优惠券id
     */
    private Long couponId;
}
