package com.ulla.modules.assets.mo;

import com.baomidou.mybatisplus.annotation.*;
import com.ulla.mybatis.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-11
 */
@Getter
@Setter
@TableName("biz_withdraw_order")
@ApiModel(value = "WithdrawOrder对象", description = "")
public class WithdrawOrderMo extends BaseEntity {

    @ApiModelProperty("出金金额")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty("状态（0-初始状态 1-已发起 2-审核中 3-审核成功 4-审核驳回 5-已出金）")
    @TableField("state")
    private Integer state;

    @ApiModelProperty("审核时间")
    @TableField("audit_time")
    private Long auditTime;

    @ApiModelProperty("审核人")
    @TableField("audit_by")
    private Long auditBy;
}
