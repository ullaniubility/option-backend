package com.ulla.modules.binance.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 币安K线数据对象生产器
 * @since 2023/2/21 16:22
 */
@Data
public class QuotationKLineCountVo implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private static QuotationKLineCountVo quotationKLineCountVo = new QuotationKLineCountVo();

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
     * 这根K线期间成交额
     */
    private Long endTime;

    public QuotationKLineCountVo() {
        super();
    }

    /**
     * 调用对象创建优化
     *
     * @return
     */
    public static QuotationKLineCountVo getInstance() {
        try {
            return (QuotationKLineCountVo)quotationKLineCountVo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new QuotationKLineCountVo();
    }

}
