package com.ulla.modules.assets.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 促销活动表
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("biz_active")
@ApiModel(value = "Active对象", description = "促销活动表")
public class ActiveMo extends BaseEntity {

    @ApiModelProperty("活动名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("促销码数量")
    @TableField("num")
    private Integer num;

    @ApiModelProperty("奖励模式 1、固定奖励；2：百分比")
    @TableField("reward_model")
    private Integer rewardModel;

    @ApiModelProperty("执行模式 1、奖励；2：支付金额扣减")
    @TableField("execute_model")
    private Integer executeModel;

    @ApiModelProperty("奖励金额（若模式为百分比奖励，这个数值为百分比）")
    @TableField("reward_amount")
    private Integer rewardAmount;

    @ApiModelProperty("生效的金额区间开始")
    @TableField("amount_range_begin")
    private Integer amountRangeBegin;

    @ApiModelProperty("生效的金额区间结束")
    @TableField("amount_range_end")
    private Integer amountRangeEnd;

    @ApiModelProperty("可使用的数量")
    @TableField("use_num")
    private Integer useNum;

    @ApiModelProperty("每天可使用的数量")
    @TableField("day_use_num")
    private Integer dayUseNum;

    @ApiModelProperty("活动开始时间")
    @TableField("begin_time")
    private Long beginTime;

    @ApiModelProperty("活动结束时间")
    @TableField("end_time")
    private Long endTime;

    @ApiModelProperty("状态 0-禁用 1-启用")
    @TableField("state")
    private Integer state;

}
