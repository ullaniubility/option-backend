package com.ulla.common.utils;

import java.io.Serializable;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/4/18 18:46
 */
@Data
public class CommonHeader implements Serializable {

    private static final long serialVersionUID = -3949488282201167943L;

    /**
     * 真实ip
     */
    private String ip;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 用户Token
     */
    private String token;

    /**
     * 设备指纹
     */
    private String fingerprint;

    /**
     * 会员等级
     */
    private String userLevel;

    public CommonHeader(String ip, Long uid, String openId, String token, String fingerprint, String userLevel) {
        this.ip = ip;
        this.uid = uid;
        this.openId = openId;
        this.token = token;
        this.fingerprint = fingerprint;
        this.userLevel = userLevel;
    }
}
