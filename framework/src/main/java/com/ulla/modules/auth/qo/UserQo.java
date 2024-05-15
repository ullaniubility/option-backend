package com.ulla.modules.auth.qo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Description {用户qo}
 * @author {clj}
 * @since {2023-2-8}
 */
@ApiModel(value = "用户Qo")
@Data
@ToString
public class UserQo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "邮箱")
    private String mail;

    @ApiModelProperty(value = "openId")
    private String openId;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "用户设备指纹")
    private String fingerprint;

    @ApiModelProperty(value = "用户头像地址")
    private String portraitAddress;

    @ApiModelProperty(value = "用户品种")
    private Integer type;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户地址")
    private String address;

    @ApiModelProperty(value = "用户生日")
    private String birth;

    @TableField(exist = false)
    @ApiModelProperty(value = "奖励金")
    private String rewards;

    @ApiModelProperty(value = "资产")
    @TableField(exist = false)
    private String balance;

    @ApiModelProperty(value = "用户自己填写的地址")
    @TableField(exist = false)
    private String userAddress;

    /**
     * 姓
     */
    @TableField("last_name")
    private String lastName;

    /**
     * 名
     */
    @TableField("first_name")
    private String firstName;

    /**
     * 邮编
     */
    @TableField("postal_code")
    private String postalCode;

    /**
     * 身份证审核状态 0.未提交 1。审核中 2。审核成功 3。 审核退回
     */
    @TableField("kyc_status")
    private Integer kycStatus;

    /**
     * 首充标识 0-新人未首充 1-已首充
     */
    @TableField("first_deposit_flag")
    private Integer firstDepositFlag;

    /**
     * 会员等级
     */
    @TableField("user_level")
    private String userLevel;

}
