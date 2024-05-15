package com.ulla.modules.business.qo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

/**
 * 弹框入参
 */
@Data
public class PopQo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long uid;

    private String pairs;

    private String tradingRange;

    private String orderCode;
    /**
     * 0没亏没赚零，1亏，2赚，3卖出
     */
    @TableField(exist = false)
    private Integer ProfitLoss;

}
