package com.ulla.modules.business.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HomePairsVo {

    /**
     * 日盈利交易对名称
     */
    private String childName;

    /**
     * 日盈利金额占比
     */
    private BigDecimal percentage;
}
