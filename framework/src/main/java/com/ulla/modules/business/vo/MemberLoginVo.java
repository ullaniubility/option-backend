package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 最新交易的用户信息
 */
@Data
public class MemberLoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String openId;

    private Long uid;
    /**
     * 用户访问ip
     */
    private String ipAddress;
    /**
     * 成交金额
     */
    private BigDecimal orderAmount;
}
