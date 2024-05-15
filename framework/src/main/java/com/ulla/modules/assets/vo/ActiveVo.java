package com.ulla.modules.assets.vo;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

/**
 * <p>
 * 促销活动表
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
public class ActiveVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull
    @Length(min = 2, max = 1000)
    private String name;
    @NotNull
    @Min(1)
    @Max(100000000)
    private Integer num;
    @NotNull
    @Min(1)
    @Max(2)
    private Integer rewardModel;
    /**
     * 促销方式： 1-奖励 2-支付金额扣减
     */
    @NotNull
    @Min(1)
    @Max(2)
    private Integer executeModel;
    @NotNull
    @Min(1)
    private Integer rewardAmount;
    @NotNull
    @Min(1)
    private Integer amountRangeBegin;
    @NotNull
    private Integer amountRangeEnd;
    @NotNull
    @Min(1)
    private Integer useNum;
    @NotNull
    @Min(1)
    private Integer dayUseNum;
    @NotNull
    private Long beginTime;
    @NotNull
    private Long endTime;

    private Integer state;
    private Long activeId;
}
