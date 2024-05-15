package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 订单结算倒计时
 * @author zhuyongdong
 * @since 2023-03-14 16:17:11
 */
@ApiModel(value = "订单结算倒计时")
@Data
public class OrderCalculationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据编号")
    private Long id;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderCode;

    /**
     * 涨跌类型 0。买跌 1。买涨
     */
    @ApiModelProperty(value = "涨跌")
    private Integer openClose;

    /**
     * 交易对
     */
    @ApiModelProperty(value = "交易对")
    private String pairs;

    /**
     * 下单时间
     */
    @JsonIgnore
    @ApiModelProperty(value = "下单时间")
    private Long orderTime;

    /**
     * 单次下单金额
     */
    @JsonIgnore
    @ApiModelProperty(value = "下单金额")
    private BigDecimal orderAmount;

    /**
     * 下单价格
     */
    @ApiModelProperty(value = "下单时刻的价格")
    private BigDecimal price;

    /**
     * 盈利百分比（%）
     */
    @ApiModelProperty(value = "盈利百分比（%）")
    private Integer profitPercent;

    /**
     * 订单状态：0，下单 1，生效 2，结束 3，撤销 4，挂单
     */
    @ApiModelProperty(value = "订单状态：0，下单 1，生效 2，结束 3，撤销 4，挂单")
    private Integer status;

    @ApiModelProperty(value = "订单类型（0，虚拟订单 1，正常订单")
    private Integer type;

    /**
     * 进度条开始时间
     */
    @ApiModelProperty(value = "进度条开始时间")
    private Long barStartTime;

    /**
     * 倒时候结束时间
     */
    @ApiModelProperty(value = "倒计时结束时间")
    private Long endTime;

    /**
     * 结算区间开始时间
     */
    @JsonIgnore
    @ApiModelProperty(value = "结算区间开始时间")
    private Long tradingStartTime;

}
