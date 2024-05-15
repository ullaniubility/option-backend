package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ulla.common.serializer.BigDecimalJsonSerializer;

import lombok.Data;

@Data
public class ActualTransactionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal todayEnterAmount;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal todayOutAmount;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal weekEnterAmount;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal weekOutAmount;

    /**
     * 入金待确认个数
     */
    private Integer enterTotal;

    /**
     * 出金待确认个数
     */
    private Integer outTotal;

}
