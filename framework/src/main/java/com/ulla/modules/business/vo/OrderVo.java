package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description {订单vo}
 * @author {clj}
 * @since {2023-2-21}
 */
@ApiModel(value = "订单vo")
@Data
public class OrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 涨跌类型 0。买跌 1。买涨
     */
    @ApiModelProperty(value = "涨跌")
    private Integer openClose;

    /**
     * 下单时间
     */
    @ApiModelProperty(value = "下单时间")
    private Long orderTime;

    /**
     * 所属交易区间id（交易区间默认固定）
     */
    @ApiModelProperty(value = "交易区间id")
    private String tradingRange;

    @TableField("order_range_id")
    private String orderRangeId;

    /**
     * 单次下单金额
     */
    @ApiModelProperty(value = "下单金额")
    private BigDecimal orderAmount;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户开放id")
    private String openId;

    /**
     * 订单类型（0，虚拟订单 1，正常订单 2，机器人订单）
     */
    @ApiModelProperty(value = "订单类型")
    private Integer type;

    /**
     * 交易对编号
     */
    @ApiModelProperty(value = "交易对")
    private Long pairsId;

    /**
     * 交易对
     */
    @ApiModelProperty(value = "交易对")
    private String pairs;

    /**
     * 下单价格
     */
    @ApiModelProperty(value = "下单时刻的价格")
    private String price;

    /**
     * 下单类型 1正常下单 4挂单
     */
    @ApiModelProperty(value = "下单类型 1正常下单 4挂单")
    private Integer status;

    /**
     * 下单时对应的行情时间
     */
    @ApiModelProperty(value = "下单时对应的行情时间")
    private Long streamTime;

    /**
     * 盈利百分比（%）
     */
    @TableField("profit_percent")
    private String profitPercent;

}
