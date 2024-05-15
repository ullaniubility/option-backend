package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class MoneyDepositVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员uid---对应的就是openId
     */
    private String uid;

    /**
     * 提现订单编号
     */
    private String orderNo;
    /**
     * 提现金额
     */
    private BigDecimal depositMonetaryAmount;
    /**
     * 提现单位
     */
    private String depositMonetaryUnit;

    /**
     * 提现平台
     */
    private String channelName;
    /**
     * 提现用户到账地址
     */
    private String address;
    /**
     * 提现订单状态 0：待付款 1：已付款 2：已完成 3：已失效 4：已失败
     */
    private Integer orderStatus;
}
