package com.ulla.modules.business.mo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("money_deposit")
@ApiModel(value = "MoneyDeposit出金对象", description = "")
public class MoneyDepositMo extends BaseEntity {

    @ApiModelProperty(value = "图标链接")
    @TableField("logo_url")
    private String logoUrl;

    @ApiModelProperty(value = "交易订单Id(法币购买第三方支付订单Id，数字币生成订单Id)")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "公司财务付款状态0：待付款 1：已付款 2：已完成 3：已失效 4：已失败")
    @TableField("order_status")
    private Integer orderStatus;

    @ApiModelProperty(value = "会员UID")
    @TableField("uid")
    private Long uid;

    @ApiModelProperty(value = "openId")
    @TableField("open_id")
    private String openId;

    @ApiModelProperty(value = "出金 - 货币单位")
    @TableField("deposit_monetary_unit")
    private String depositMonetaryUnit;

    @ApiModelProperty(value = "出金金额")
    @TableField("deposit_monetary_amount")
    private BigDecimal depositMonetaryAmount;

    @ApiModelProperty(value = "收币地址")
    @TableField("address")
    private String address;

    @ApiModelProperty(value = "收币地址--小写")
    @TableField("address_lower")
    private String addressLower;

    @ApiModelProperty(value = "数字币交易hash")
    @TableField("transaction_hash")
    private String transactionHash;

    @ApiModelProperty(value = "币链(实际到账数字币有值)")
    @TableField("net")
    private String net;

    @ApiModelProperty(value = "币种符号(实际到账数字币有值)")
    @TableField("symbol")
    private String symbol;

    @ApiModelProperty(value = "到账币种数量- 实际到账数字币有值")
    @TableField("currency_amount")
    private BigDecimal currencyAmount;

    @ApiModelProperty(value = "到账币价格--交易时价格---实际到账数字币有值")
    @TableField("currency_price")
    private BigDecimal currencyPrice;

    @ApiModelProperty(value = "支付渠道名称")
    @TableField("channel_name")
    private String channelName;

    @ApiModelProperty(value = "会员账号")
    @TableField("mail")
    private String mail;

    @ApiModelProperty(value = "审核拒绝原因")
    @TableField("remark")
    private String remark;

}
