package com.ulla.modules.business.mo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Description {用户资金}
 * @author {clj}
 * @since {2023-2-16}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_order")
@ToString
public class OrderMo extends BaseEntity {

    /**
     * 涨跌类型 0。买跌 1。买涨
     */
    @TableField("open_close")
    private Integer openClose;

    /**
     * 下单时间
     */
    @TableField("order_time")
    private Long orderTime;

    /**
     * 事件时间
     */
    @TableField("stream_time")
    private Long streamTime;

    /**
     * 所属交易区间id（交易区间默认固定）
     */
    @TableField("trading_range")
    private String tradingRange;

    /**
     * 单次下单金额
     */
    @TableField("order_amount")
    private BigDecimal orderAmount;

    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;

    /**
     * 订单类型（0，虚拟订单 1，正常订单 2，机器人订单）
     */
    @TableField("type")
    private Integer type;

    /**
     * 订单状态：0，下单 1，生效 2，结束 3，撤销 4，挂单
     */
    @TableField("status")
    private Integer status;

    /**
     * 交易对编号
     */
    @TableField("pairs_id")
    private Long pairsId;

    /**
     * 交易对
     */
    @TableField("pairs")
    private String pairs;

    /**
     * 下单价格
     */
    @TableField("price")
    private String price;

    /**
     * 盈亏 0，亏 1，赚
     */
    @TableField("if_profit")
    private Integer ifProfit;

    /**
     * 订单号
     */
    @TableField("order_code")
    private String orderCode;

    /**
     * 盈利金额--单笔
     */
    @TableField("benefit")
    private BigDecimal benefit;

    /**
     * 交易结束时间
     */
    @TableField("end_time")
    private Long endTime;

    /**
     * 交易结束的单价
     */
    @TableField("end_price")
    private String endPrice;

    /**
     * 订单区间区间id
     */
    @TableField("order_range_id")
    private String orderRangeId;

    /**
     * 盈利百分比（%）
     */
    @TableField("profit_percent")
    private String profitPercent;

    /**
     * 出金额度
     */
    @TableField("withdrawal_amount")
    private BigDecimal withdrawalAmount;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

}
