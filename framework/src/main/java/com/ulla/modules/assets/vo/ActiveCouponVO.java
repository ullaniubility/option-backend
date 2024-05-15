package com.ulla.modules.assets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 查询促销码返回VO
 * </p>
 *
 * @author michael
 * @since 2023-03-23
 */
@Data
public class ActiveCouponVO implements Serializable {


    /**
     *主键ID
     */
    private Long id;
    /**
     * 优惠券码
     */
    private String couponCode;
    /**
     * 活动id
     */
    private Integer activeId;
    /**
     * 是否使用 0-未使用 1-已使用
     */
    private Integer useFlag;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 提币订单编号
     */
    private String depositOrderNo;
    /**
     * 使用时间
     */
    private Long useTime;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     *创建时间
     */
    private Long createTime;
    /**
     * 修改人
     */
    private Long updateBy;
    /**
     *修改时间
     */
    private Long updateTime;
    /**
     * 删除标志 0.未删除 1. 删除
     */
    private Boolean deleteFlag;

    /**
     * 活动名称
     */
    private String name;
    /**
     * 促销码数量
     */
    private Integer num;
    /**
     * 奖励模式（0-无奖励 1-固定奖励 2-百分比奖励）
     */
    private Integer rewardModel;
    /**
     * 奖励金额（若模式为百分比奖励，这个数值为百分比）
     */
    private Integer rewardAmount;
    /**
     * 生效的金额区间开始
     */
    private Integer amountRangeBegin;
    /**
     * 生效的金额区间结束
     */
    private Integer amountRangeEnd;
    /**
     * 可使用的数量
     */
    private Integer useNum;
    /**
     * 每天可使用的数量
     */
    private Integer dayUseNum;
    /**
     * 活动开始时间
     */
    private Long beginTime;
    /**
     * 活动结束时间
     */
    private Long endTime;
    /**
     * 状态 0-禁用 1-启用
     */
    private Integer state;

}
