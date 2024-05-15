package com.ulla.modules.auth.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description {用户登录日志实体类}
 * @author {clj}
 * @since {2023-2-9}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_user_login_info")
public class UserLoginInfoMo extends BaseEntity {

    /**
     * 用户自己输入的地址(只做记录不做展示)
     */
    @TableField("ip")
    private String ip;

    /**
     * ip解析出的地址
     */
    @TableField("area")
    private String area;

    /**
     * 是否游客用户 1.是 0. 否
     */
    @TableField("if_guest")
    private Integer ifGuest;

    /**
     * 登录平台
     */
    @TableField("platform")
    private String platform;

    /**
     * 设备指纹
     */
    @TableField("fingerprint")
    private String fingerprint;

    /**
     * 关联userId
     */
    @TableField("uid")
    private Long uid;

}
