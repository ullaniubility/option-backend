package com.ulla.modules.auth.vo;

import java.io.Serializable;

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
public class UserEditVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "邮编")
    private String postalCode;

    @ApiModelProperty(value = "地区（市/区）")
    private String userAddress;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "用户头像地址")
    private String portraitAddress;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "生日")
    private String birth;

    @ApiModelProperty(value = "昵称")
    private String nickName;

}
