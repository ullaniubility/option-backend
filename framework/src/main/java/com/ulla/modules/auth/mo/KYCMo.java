package com.ulla.modules.auth.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_kyc")
public class KYCMo extends BaseIdEntity {

    private Long uid;

    private String openId;

    /**
     * 审核状态0.发起 1。审核通过 2。审核退回
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核意见
     */
    @TableField("comment")
    private String comment;

    /**
     * 证件正面地址
     */
    @TableField("positive_link")
    private String positiveLink;

    /**
     * 证件反面地址
     */
    @TableField("side_link")
    private String sideLink;

    /**
     * 证件类型id
     */
    @TableField("certificate_id")
    private Long certificateId;

    /**
     * 证件号码
     */
    @TableField("certificate_num")
    private String certificateNum;

    /**
     * 证件类型名称
     */
    @TableField("certificate_name")
    private String certificateName;

    /**
     * 审核时间
     */
    @TableField("verify_time")
    private Long verifyTime;

    /**
     * 发起时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 国籍
     */
    @TableField("country")
    private String country;

    /**
     * 用户真实姓名
     */
    @TableField("real_name")
    private String realName;
}
