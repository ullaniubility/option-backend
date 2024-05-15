package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class OrderSearchVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属交易区间id（交易区间默认固定）
     */
    @TableField("trading_range")
    private String tradingRange;

    /**
     * 交易对
     */
    @TableField("pairs")
    private String pairs;

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
     * 区交易盈亏0亏1赚
     */
    private Integer totalProfit;

    /**
     * 同区交易盈利总金额
     */
    private BigDecimal totalBenefit;

    /**
     * 同区交易下单总价格
     */
    private BigDecimal totalPrice;

    /**
     * 订单交易前15秒到该交易区间结束的60秒，总共75秒的交易行情数据
     */
    private List<Quotation> quotation;

    @Getter
    @Setter
    public static class Quotation implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 行情时间
         */
        private Long quotationTime;

        /**
         * 行情价格
         */
        private String quotationPrice;

    }

    /**
     * 涨跌类型 0。买跌 1。买涨
     */
    private Integer openClose;

    /**
     * 下单时间
     */
    private Long orderTime;

    private String orderRangeId;

    /**
     * 单次下单金额
     */
    private BigDecimal orderAmount;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单类型（0，虚拟订单 1，正常订单 2，机器人订单）
     */
    private Integer type;

    /**
     * 订单状态：0，下单 1，生效 2，结束 3，撤销
     */
    private Integer status;

    /**
     * 下单价格
     */
    private BigDecimal price;

    /**
     * 盈亏 0，亏 1，赚
     */
    private Integer ifProfit;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 盈利金额--单笔
     */
    private BigDecimal benefit;

}
