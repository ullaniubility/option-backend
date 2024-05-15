package com.ulla.binance.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.binance.mybatis.BaseIdEntity;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 币安K线数据对象生产器
 * @since 2023/2/21 16:22
 */
@Data
@TableName("ba_quotation_kline_origin")
public class QuotationKLineOriginMo extends BaseIdEntity implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private static QuotationKLineOriginMo quotationProductMo = new QuotationKLineOriginMo();

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
     * 这根K线的起始时间
     */
    @TableField("k_start_time")
    private Long kStartTime;
    /**
     * 这根K线的结束时间
     */
    @TableField("k_end_time")
    private Long kEndTime;

    /**
     * K线间隔
     */
    @TableField("k_line_type")
    private String kLineType;

    /**
     * 这根K线期间第一笔成交ID
     */
    @TableField("fist_tran_id")
    private Long fistTranId;

    /**
     * 这根K线期间末一笔成交ID
     */
    @TableField("end_tran_id")
    private Long endTranId;

    /**
     * 这根K线期间末一笔成交价
     */
    @TableField("close_price")
    private String closePrice;
    /**
     * 这根K线期间第一笔成交价
     */
    @TableField("open_price")
    private String openPrice;

    /**
     * 这根K线期间最高成交价
     */
    @TableField("high_price")
    private String highPrice;
    /**
     * 这根K线期间最低成交价
     */
    @TableField("low_price")
    private String lowPrice;
    /**
     * 这根K线期间成交量
     */
    @TableField("turnover_num")
    private String turnoverNum;
    /**
     * 这根K线期间成交笔数
     */
    @TableField("turnover_amount")
    private Long turnoverAmount;
    /**
     * 这根K线是否完结(是否已经开始下一根K线) 0否，1是
     */
    @TableField("close_flag")
    private Integer closeFlag;
    /**
     * 这根K线期间成交额
     */
    @TableField("tran_limit")
    private String tranLimit;
    /**
     * 主动买入的成交量
     */
    @TableField("buying_volume")
    private String buyingVolume;
    /**
     * 主动买入的成交额
     */
    @TableField("tran_volume")
    private String tranVolume;

    public QuotationKLineOriginMo() {
        super();
    }

    /**
     * 调用对象创建优化
     *
     * @return
     */
    public static QuotationKLineOriginMo getInstance() {
        try {
            return (QuotationKLineOriginMo)quotationProductMo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new QuotationKLineOriginMo();
    }

}
