package com.ulla.modules.assets.mo;

import com.baomidou.mybatisplus.annotation.*;
import com.ulla.mybatis.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 入金配置表
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("sys_deposit_config")
@ApiModel(value = "DepositConfig对象", description = "入金配置表")
public class DepositConfigMo extends BaseEntity {

    @ApiModelProperty("配置金额")
    @TableField("config_amount")
    private Integer configAmount;

    @ApiModelProperty("入金区间开始（包含）")
    @TableField("range_begin")
    private Integer rangeBegin;

    @ApiModelProperty("入金区间结束（不包含）")
    @TableField("range_end")
    private Integer rangeEnd;

    @ApiModelProperty("奖励模式")
    @TableField("reward_model")
    private Integer rewardModel;

    @ApiModelProperty("奖励金额（若模式为百分比奖励，这个数值为百分比）")
    @TableField("reward_amount")
    private Integer rewardAmount;

    @ApiModelProperty("按钮背景颜色")
    @TableField("button_background_color")
    private String buttonBackgroundColor;

    @ApiModelProperty("按钮边框颜色")
    @TableField("button_border_color")
    private String buttonBorderColor;

    @ApiModelProperty("新人活动标识（0-关闭 1-开启）")
    @TableField("newcomer_active_flag")
    private Integer newcomerActiveFlag;

    @ApiModelProperty("活动时间（单位：分）")
    @TableField("active_time")
    private Integer activeTime;

    @ApiModelProperty("活动奖励模式")
    @TableField("active_reward_model")
    private Integer activeRewardModel;

    @ApiModelProperty("活动奖励金额（若模式为百分比奖励，这个数值为百分比）")
    @TableField("active_reward_amount")
    private Integer activeRewardAmount;

    @ApiModelProperty("配置状态")
    @TableField("state")
    private Integer state;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;


}
