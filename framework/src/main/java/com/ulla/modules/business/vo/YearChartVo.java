package com.ulla.modules.business.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class YearChartVo {

    private String months;

    private Integer count;

    private String symbol;

    private String net;

    private String symbolNames;

    // 入金:公司实际到账金额
    private BigDecimal currencyAmount;

    private BigDecimal orderAmount;

    private BigDecimal benefit;

    private String pairs;

    private Long pairsId;

    private Long startTime;

    private Long endTime;

    private Long days;

}
