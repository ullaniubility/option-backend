package com.ulla.modules.binance.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ulla.mybatis.BaseIdEntity;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 币安行数数据对象生产器
 * @since 2023/2/21 16:22
 */
@Data
public class QuotationProductMo extends BaseIdEntity implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private static QuotationProductMo quotationProductMo = new QuotationProductMo();

    /**
     * 事件时间
     */
    @TableField("stream_time")
    private Long streamTime;
    /**
     * 标的交易对
     */
    @TableField("symbol")
    private String symbol;
    /**
     * 最新成交价格
     */
    @TableField("close_price")
    private String closePrice;
    /**
     * 24小时前开始第一笔成交价格
     */
    @TableField("open_price")
    private String openPrice;

    /**
     * 24小时内最高成交价
     */
    @TableField("high_price")
    private String highPrice;
    /**
     * 24小时内最低成交价
     */
    @TableField("low_price")
    private String lowPrice;
    /**
     * 24小时成交量
     */
    @TableField("turnover_num")
    private String turnoverNum;
    /**
     * 24小时成交额(标的数量)
     */
    @TableField("turnover_amount")
    private String turnoverAmount;

    /**
     * 统一数据编号
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 结算区间编号
     */
    @TableField("trading_range_id")
    private String tradingRangeId;

    /**
     * 下单区间编号
     */
    @TableField("order_range_id")
    private String orderRangeId;

    /**
     * 下单区间开始时间
     */
    @TableField("order_start_time")
    private Long orderStartTime;

    /**
     * 下单区间结束时间
     */
    @TableField("order_end_time")
    private Long orderEndTime;

    /**
     * 结算区间开始时间
     */
    @TableField("trading_start_time")
    private Long tradingStartTime;

    /**
     * 结算区间结束时间
     */
    @TableField("trading_end_time")
    private Long tradingEndTime;

    public QuotationProductMo() {
        super();
    }

    /**
     * 调用对象创建优化
     *
     * @return
     */
    public static QuotationProductMo getInstance() {
        try {
            return (QuotationProductMo)quotationProductMo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new QuotationProductMo();
    }

}
