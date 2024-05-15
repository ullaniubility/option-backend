package com.ulla.modules.assets.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 用户资产流水表
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-06
 */
@Getter
@Setter
@TableName("biz_balance_error_log")
@ApiModel(value = "BalanceErrorLog对象", description = "用户资产流水表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceErrorLogMo extends BaseEntity {

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("变动的真实金额")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty("变动的奖金金额")
    @TableField("bonus_amount")
    private BigDecimal bonusAmount;

    @ApiModelProperty("业务订单编号")
    @TableField("business_no")
    private String businessNo;

    @ApiModelProperty("业务类型（1-入金 2-提现 3-交易 4-系统奖励）")
    @TableField("business_type")
    private Integer businessType;

    @ApiModelProperty("异常记录处理标识 0-未处理 1-已处理 ")
    @TableField("dispose_flag")
    private Integer disposeFlag;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;
}
