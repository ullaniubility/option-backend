package com.ulla.modules.business.vo;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FinanceVo {
    @ApiModelProperty("订单号")
    private String logNo;

    @ApiModelProperty("用户账号")
    private String mail;

    private String uid;

    @ApiModelProperty("入金金额")
    private BigDecimal amount;

    @ApiModelProperty("入金方式")
    private String channelName;

    private Long createTime;

    @ApiModelProperty("0：待付款\n" + "1：已付款\n" + "2：已完成\n" + "3：已失效\n" + "4：已失败")
    private Integer orderStatus;

    @ApiModelProperty("待入金金额")
    private BigDecimal waitTotal;

    @ApiModelProperty("已入金金额")
    private BigDecimal alreadyTotal;

}
