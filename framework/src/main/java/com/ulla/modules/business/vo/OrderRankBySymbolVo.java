package com.ulla.modules.business.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 订单排名
 * @author zhuyongdong
 * @since 2023-04-04 13:26:49
 */
@Data
public class OrderRankBySymbolVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "在线人数")
    private Integer todayUserCount;

    @ApiModelProperty(value = "开放订单额")
    private Integer todayOrderTotalAmount;

    @ApiModelProperty(value = "开放订单")
    private Integer todayOrderCount;

    @ApiModelProperty(value = "交易对")
    private String pairs;

    @ApiModelProperty(value = "订单数量")
    private Integer orderCount;

    @ApiModelProperty(value = "用户数量")
    private Integer userCount;

    @ApiModelProperty(value = "交易量")
    private Integer orderTotalAmount;

    @ApiModelProperty(value = "涨百分比")
    private Integer risePercent;

    @ApiModelProperty(value = "跌百分比")
    private Integer dropPercent;

}
