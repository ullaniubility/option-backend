package com.ulla.modules.payment.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CallResponseVO {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "支付通道订单号")
    private String payOrderId;

    @ApiModelProperty(value = "订单副id")
    private String subOrderId;

    @TableField("channel_ident")
    @ApiModelProperty(value = "渠道唯一标识")
    private String channelIdent;

    @ApiModelProperty(value = "订单类型 买或卖  buy sell")
    private String type;

    @ApiModelProperty(value = "金额 - 第三方支付平台实际支付的值")
    private BigDecimal value;



    @ApiModelProperty(value = "法币种类（usd-美元,EUR-英镑 等）")
    private String currency;

    @ApiModelProperty(value = "订单当前状态-OrderRecordState类")
    private Integer state;

    @ApiModelProperty(value = "到账地址")
    private String toAddress;

    @ApiModelProperty(value = "到账金额 - 公司到账的钱(如果是币的话就是数量)")
    private String tokenAmount;

    @ApiModelProperty(value = "到账类型-ETH等")
    private String net;

    @ApiModelProperty(value = "symbol")
    private String symbol;

    @ApiModelProperty(value = "创建人-可以为空")
    private String userId;

    @ApiModelProperty(value = "创建订单的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @TableField("payment_link")
    @ApiModelProperty(value = "支付链接")
    private String paymentLink;

    @TableField("call_url")
    @ApiModelProperty(value = "回调链接")
    private String callUrl;

    @TableField("tx_hash")
    @ApiModelProperty(value = "交易hash")
    private String txHash;

    @ApiModelProperty(value = "换算价格，即虚拟币的实时价格")
    private BigDecimal conversionPrice;

}
