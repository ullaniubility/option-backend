package com.ulla.modules.assets.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@EqualsAndHashCode(callSuper = true)
@TableName("biz_active_coupon")
@ApiModel(value = "ActiveCoupon对象", description = "促销优惠券表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveCouponMo extends BaseEntity {

    @ApiModelProperty("优惠券码")
    @TableField("coupon_code")
    private String couponCode;

    @ApiModelProperty("活动id")
    @TableField("active_id")
    private Long activeId;

    @ApiModelProperty("是否使用 0-未使用 1-已使用")
    @TableField("use_flag")
    private Integer useFlag;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("提币订单编号")
    @TableField("deposit_order_no")
    private String depositOrderNo;

    @ApiModelProperty("使用时间")
    @TableField("use_time")
    private Long useTime;
}
