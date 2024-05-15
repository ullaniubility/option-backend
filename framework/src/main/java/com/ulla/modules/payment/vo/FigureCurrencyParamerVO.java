package com.ulla.modules.payment.vo;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建法币支付订单时需要的参数VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FigureCurrencyParamerVO {

    @ApiModelProperty(name = "推荐人")
    private Long inviteId;
    @ApiModelProperty(name = "页面选择的按钮id-校验页面选择的入金金额和配置的按钮金额是否一致")
    private String buttonCode;
    @ApiModelProperty(name = "入金金额 - 页面选择的入金金额")
    @NotNull(message = "支付金额为空")
    private BigDecimal estimatedDepositAmount;
    @ApiModelProperty(name = "奖励配置Id")
    private String rewardCode;
    @ApiModelProperty(name = "支付渠道类型: 0：法币购买 - 实际到账法币 1：法币购买 - 实际到账数字货币 2：数字货币购买 - 实际到账数字货币")
    @NotNull(message = "支付渠道为空")
    private Integer channelType;
    @ApiModelProperty(name = "币链(实际到账数字币有值)")
    @NotNull(message = "币种信息为空")
    private String net;
    @ApiModelProperty(name = "币种符号(实际到账数字币有值)")
    @NotNull(message = "币种信息为空")
    private String symbol;
    @ApiModelProperty(name = "是否使用促销代码 0：未使用 1：使用")
    @NotNull(message = "促销信息状态为空")
    private Integer isUsePreferential;
    @ApiModelProperty(name = "促销码编号")
    private Long couponId;
    @ApiModelProperty(name = "促销代码")
    private String preferentialCode;
    @ApiModelProperty(name = "促销优惠金额,默认为0")
    private BigDecimal preferentialAmount = BigDecimal.ZERO;
    @ApiModelProperty(name = "收币地址")
    @NotNull(message = "币种地址为空")
    private String address;
    @ApiModelProperty(name = "到账币种数量- 实际到账数字币有值")
    private BigDecimal currencyAmount;

}
