package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 订单结算倒计时展示
 * @author zhuyongdong
 * @since 2023-03-14 16:17:11
 */
@ApiModel(value = "订单结算倒计时展示")
@Data
public class OrderBeforeCalculationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据编号")
    private Long id;
    @ApiModelProperty(value = "订单号")
    private String orderCode;
    @ApiModelProperty(value = "涨跌")
    private Integer openClose;
    @ApiModelProperty(value = "交易对")
    private String pairs;
    @ApiModelProperty(value = "下单时间")
    private Long orderTime;
    @ApiModelProperty(value = "下单金额")
    private BigDecimal orderAmount;
    @ApiModelProperty(value = "单次盈亏金额")
    private BigDecimal phaseAmount;
    @ApiModelProperty(value = "下单时刻的价格")
    private BigDecimal price;
    @ApiModelProperty(value = "订单状态：0，下单 1，生效")
    private Integer status;
    @ApiModelProperty(value = "倒计时开始金额")
    private BigDecimal startPrice;
    @ApiModelProperty(value = "进度条开始时间")
    private Long barStartTime;
    @ApiModelProperty(value = "倒计时开始时间")
    private Long startTime;
    @ApiModelProperty(value = "倒计时结束时间,进度条结束时间")
    private Long endTime;
    @ApiModelProperty(value = "结算区间开始时间")
    private Long tradingStartTime;
    @ApiModelProperty(value = "结算区间结束时间")
    private Long tradingEndTime;

}
