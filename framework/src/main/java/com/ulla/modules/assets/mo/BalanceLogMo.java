package com.ulla.modules.assets.mo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>
 * 用户资产流水表
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("biz_balance_log")
@ApiModel(value = "BalanceLog对象", description = "用户资产流水表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceLogMo extends BaseEntity {

    @ApiModelProperty("转移人id，为0的时候是系统")
    @TableField("from_user_id")
    private Long fromUserId;

    @ApiModelProperty("余额")
    @TableField("balance")
    private BigDecimal balance;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("流水编号")
    @TableField("log_no")
    private String logNo;

    @ApiModelProperty("流水编号")
    @TableField("business_no")
    private String businessNo;

    @ApiModelProperty("接收人id，为0的时候是系统")
    @TableField("to_user_id")
    private Long toUserId;

    @ApiModelProperty("业务类型（1-入金 2-提现 3-交易 4-系统奖励 5-后台管理员操作）")
    @TableField("business_type")
    private Integer businessType;

    @ApiModelProperty("流水类型（ 0。奖励金 1。真实资金 2。机器人资金 3、虚拟资金）")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("变动的金额")
    @TableField("amount")
    private BigDecimal amount;

}
