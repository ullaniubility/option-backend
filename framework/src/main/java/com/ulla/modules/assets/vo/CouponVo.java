package com.ulla.modules.assets.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ulla.common.serializer.BigDecimalJsonSerializer;

import lombok.*;

/**
 * <p>
 * 促销优惠券表
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String couponCode;
    private Long activeId;
    private Integer useFlag;
    private Long userId;
    private String depositOrderNo;
    private Long useTime;
    private Integer rewardModel;
    private Integer executeModel;
    private Integer rewardAmount;
    private Integer useNum;
    private Integer dayUseNum;
    private Long beginTime;
    private Long endTime;
    private Integer state;
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal couponDeductAmount;
}
