package com.ulla.modules.binance.vo;

import java.io.Serializable;

import com.ulla.mybatis.BaseIdEntity;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 币安K线数据对象生产器
 * @since 2023/2/21 16:22
 */
@Data
public class QuotationProductVo extends BaseIdEntity implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    /**
     * 事件时间
     */
    private Long streamTime;
    /**
     * 标的交易对
     */
    private String symbol;
    /**
     * 这根K线的起始时间
     */
    private Long kStartTime;
    /**
     * 这根K线的结束时间
     */
    private Long kEndTime;

    /**
     * 这根K线期间末一笔成交价
     */
    private String closePrice;
    /**
     * 这根K线期间第一笔成交价
     */
    private String openPrice;

    /**
     * 这根K线期间最高成交价
     */
    private String highPrice;
    /**
     * 这根K线期间最低成交价
     */
    private String lowPrice;
    /**
     * 这根K线期间成交量
     */
    private String turnoverNum;
    /**
     * 这根K线期间成交笔数
     */
    private Long turnoverAmount;
    /**
     * 这根K线期间成交额
     */
    private String tranLimit;
    /**
     * 这根K线是否完结 0否，1是
     */
    private Integer closeFlag;
    /**
     * 统一数据编号
     */
    private String uuid;

    /**
     * 结算区间编号
     */
    private String tradingRangeId;

    /**
     * 下单区间编号
     */
    private String orderRangeId;

    /**
     * 下单区间开始时间
     */
    private Long orderStartTime;

    /**
     * 下单区间结束时间
     */
    private Long orderEndTime;

    /**
     * 结算区间开始时间
     */
    private Long tradingStartTime;

    /**
     * 结算区间结束时间
     */
    private Long tradingEndTime;

}
