package com.ulla.modules.business.vo;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ulla.common.serializer.BigDecimalJsonSerializer;

import lombok.Data;

@Data
public class PairVo {

    private String pairs;

    private Integer status;

    private Integer type;

    private Integer ifProfit;

    private BigDecimal orderAmount;

    private BigDecimal benefit;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal profitMoney;
}
