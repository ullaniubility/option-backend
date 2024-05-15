package com.ulla.modules.assets.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * 促销码查询参数VO
 * </p>
 *
 * @author michael
 * @since 2023-03-23
 */
@Data
public class ActiveParameterVO implements Serializable {

    /**
     * 活动id
     */
    private Integer activeId;
    /**
     * 绑定的用户id
     */
    private Long userId;

    /**
     * 订单编号
     */
    private String depositOrderNo;

    /**
     * 促销方式：1-固定奖励 2-百分比奖励
     */
    private Integer rewardModel;

    /**
     * 促销方式： 1-奖励 2-支付金额扣减
     */
    private Integer executeModel;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 是否使用促销码： 0-未使用 1-已使用
     */
    private Integer useFlag;

    /**
     * 促销码状态 0-禁用 1-启用
     */
    private Integer state;

    /**
     * 下单开始时间
     */
    private Long startTime;

    /**
     * 下单结束时间
     */
    private Long endTime;

    /**
     * 活动开始时间
     */
    private Long beginTime;

    /**
     * 活动结束时间
     */
    private Long overTime;

    /**
     * 当前页
     */
    private Integer page;
    /**
     * 每页展示数量
     */
    private Integer pageSize;

}
