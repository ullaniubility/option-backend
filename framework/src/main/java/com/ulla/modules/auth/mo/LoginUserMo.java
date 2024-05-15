package com.ulla.modules.auth.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@TableName("biz_login_user")
@ToString
public class LoginUserMo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private Long id;

    private String token;

    private String openId;

    private Long uid;

    private String permissions;

    private Long loginTime;

    private String userLevel;

}
