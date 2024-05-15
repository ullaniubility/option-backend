package com.ulla.modules.auth.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_third_config")
public class ThirdConfigMo extends BaseEntity {

    /**
     * 第三方名称
     */
    @TableField("name")
    private String name;

    /**
     * 客户端id：对应各平台的appKey
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 客户端Secret：对应各平台的appSecret，苹果的secret是动态的
     */
    @TableField("client_secret")
    private String clientSecret;

    /**
     * 登录成功后的回调地址
     */
    @TableField("redirect_uri")
    private String redirectUri;

    /**
     * 支持自定义授权平台的 scope 内容
     */
    @TableField("scopes")
    private String scopes;

    /**
     * scopes分隔符
     */
    @TableField("scope_separator")
    private String scopeSeparator;

    /**
     * 以下为苹果授权登录的专用参数
     */
    @TableField("kid")
    private String kid;
    @TableField("team_id")
    private String teamId;
    @TableField("private_key")
    private String privateKey;

}
