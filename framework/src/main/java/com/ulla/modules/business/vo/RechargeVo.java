package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ulla.common.serializer.BigDecimalJsonSerializer;

import lombok.Data;

@Data
public class RechargeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long createTime;

    private Integer orderStatus;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositMonetaryAmount;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal estimatedDepositAmount;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rewardAmount;

    private Long uid;

    private String net;

    private String remark;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal balance;

    private Integer rechargeType;

    private Integer businessType;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal amount;

    private Integer type;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal eoPoint;

}
