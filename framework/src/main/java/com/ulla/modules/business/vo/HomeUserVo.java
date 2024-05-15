package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class HomeUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前在线人数
     */
    private Integer onlineMember;

    /**
     * 今日新增新用户人数
     */
    private Integer addMember;

    /**
     * 当前开放订单数
     */
    private Integer openOrder;

    /**
     * 当前开放订单额
     */
    private BigDecimal openAmount;

    /**
     * 会员总数
     */
    private Integer totalMember;
}
