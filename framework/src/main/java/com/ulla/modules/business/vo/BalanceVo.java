package com.ulla.modules.business.vo;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BalanceVo {
    @ApiModelProperty("订单号")
    private String logNo;

    @ApiModelProperty("用户账号")
    private String mail;

    private String uid;

    @ApiModelProperty("变动金额")
    private BigDecimal amount;

    @ApiModelProperty("方式：1入金2提现3交易4奖励")
    private Integer businessType;

    private Long createTime;

    @ApiModelProperty("变动后余额")
    private BigDecimal balance;

    @ApiModelProperty("变动前余额")
    private BigDecimal beforeBalance;

}
