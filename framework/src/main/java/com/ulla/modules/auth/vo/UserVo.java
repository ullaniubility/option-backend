package com.ulla.modules.auth.vo;

import java.io.Serializable;

import com.ulla.constant.VerificationConstant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description {用户vo}
 * @author {clj}
 * @since {2023-2-8}
 */
@ApiModel(value = "用户Vo")
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "手机号/邮箱")
    private String mail;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "openId")
    private String openId;

    @ApiModelProperty(value = "用户设备指纹")
    private String fingerprint;

    @ApiModelProperty(value = "用户头像地址")
    private String portraitAddress;

    @ApiModelProperty(value = "用户品种")
    private Integer type;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "验证码类型")
    private VerificationConstant.Verify_Type codeType;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "生日")
    private String birth;

    @ApiModelProperty(value = "用户输入地址(不做展示)")
    private String userAddress;

    @ApiModelProperty(value = "新密码")
    private String newPassword;

    @ApiModelProperty(value = "邮编")
    private String postalCode;

}
