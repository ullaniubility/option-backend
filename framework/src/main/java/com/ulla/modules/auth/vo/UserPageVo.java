package com.ulla.modules.auth.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 */
@ApiModel(value = "用户Vo")
@Data
public class UserPageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户uid")
    private Long uid;

    @ApiModelProperty(value = "openId")
    private String openId;

    @ApiModelProperty(value = "邮箱")
    private String mail;

    @ApiModelProperty(value = "邮箱")
    private String phone;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "身份类型 0.demo用户 ， 1.正式用户")
    private Integer type;

    @ApiModelProperty(value = "交易总笔数")
    private Integer orderCount;

    @ApiModelProperty(value = "交易总量")
    private BigDecimal orderCountAmount;

    @ApiModelProperty(value = "用户真实余额")
    private BigDecimal realBalance;

    @ApiModelProperty(value = "用户奖励余额")
    private BigDecimal bonusBalance;

    @ApiModelProperty(value = "用户虚拟余额")
    private BigDecimal virtuallyBalance;

    @ApiModelProperty(value = "份证审核状态 0.未提交 1。审核中 2。审核成功 3。 审核退回")
    private Integer kycStatus;

    @ApiModelProperty(value = "删除标志 0.未删除 1. 删除")
    private Integer deleteFlag;

    @ApiModelProperty(value = "注册时间")
    private Long createTime;

    @ApiModelProperty(value = "国家")
    private String area;

    private String nickName;

    private String wallectAddress;

}
