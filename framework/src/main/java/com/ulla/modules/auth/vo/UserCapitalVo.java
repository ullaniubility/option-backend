package com.ulla.modules.auth.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserCapitalVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /***
     * 真实余额
     */
    private BigDecimal trueBalance;

    /**
     * 奖金余额
     */
    private BigDecimal bonusBalance;

    /**
     * eo积分
     */
    private Integer eo;

    /**
     * 模拟余额
     */
    private BigDecimal simulatedBalance;

    /**
     * 历史入金总额
     */
    private BigDecimal totalInvestment;

    /**
     * 历史出金总额
     */
    private BigDecimal totalDisbursement;
}
