package com.ulla.modules.assets.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * <p>
 * 资金流水表列表查询参数VO
 * </p>
 *
 * @author michael
 * @since 2023-03-22
 */
@Data
public class BalanceLogParameterVO implements Serializable {

    /**
     * 流水类型（ 0。奖励金 1。真实资金 2。机器人资金 3、虚拟资金）
     */
    private Integer type = 1;

    /**
     * 转移人id，为0的时候是系统
     */
    private Long fromUserId;

    /**
     * 接收人id，为0的时候是系统
     */
    private Long toUserId;

    private Long otherUserId;

    /**
     * 变动的金额
     */
    private BigDecimal minAmount;

    /**
     * 变动的金额
     */
    private BigDecimal maxAmount;

    /**
     * 创建时间
     */
    private Long startTime;

    /**
     * 创建时间
     */
    private Long endTime;

    /**
     * 当前页
     */
    private Integer page;
    /**
     * 每页展示数量
     */
    private Integer pageSize;

}
