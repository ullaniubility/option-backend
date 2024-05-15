package com.ulla.modules.auth.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 用户等级
 * @author zhuyongdong
 * @since 2023-05-16 20:12:04
 */
@Data
@TableName("biz_user_level")
public class UserLevelMo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号")
    @TableField("sort_num")
    private String sortNum;

    @ApiModelProperty(value = "会员等级")
    @TableField("level")
    private String level;

    @ApiModelProperty(value = "入金金额（$），升级条件")
    @TableField("promotion_conditions")
    private String promotionConditions;

    @ApiModelProperty(value = "允许同时进行的最大交易数量（笔数）")
    @TableField("quota_num")
    private String quotaNum;

    @ApiModelProperty(value = "单笔最大交易金额（$）")
    @TableField("quota_price")
    private String quotaPrice;

    @ApiModelProperty(value = "增加资产收益百分比（%）")
    @TableField("asset_income")
    private String assetIncome;

    @ApiModelProperty(value = "是否禁用 0否 1是")
    @TableField("off_flag")
    private String offFlag;

}
