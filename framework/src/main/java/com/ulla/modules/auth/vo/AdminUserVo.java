package com.ulla.modules.auth.vo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @Description 后台用户视图
 * @author zhuyongdong
 * @since 2023-03-30 17:56:55
 */
@Data
public class AdminUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long uid;

    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("mail")
    private String mail;

    /**
     * 身份类型 0.demo用户 ， 1.正式用户 , 2. 机器人
     */
    @TableField("type")
    private Integer type;

    /**
     * 用户唯一标识
     */
    @TableField("open_id")
    private String openId;

    /**
     * 支付密码
     */
    @TableField("pay_password")
    private String payPassword;

    /**
     * 邀请码
     */
    @TableField("wel_code")
    private String welCode;

    /**
     * 证件号码
     */
    @TableField("card_no")
    private String cardNo;

    /**
     * 生日
     */
    @TableField("birth")
    private String birth;

    /**
     * 性别
     */
    @TableField("gender")
    private String gender;

    /**
     * 邀请人ID
     */
    @TableField("invite_id")
    private Long inviteId;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 姓
     */
    @TableField("last_name")
    private String lastName;

    /**
     * 名
     */
    @TableField("first_name")
    private String firstName;

    /**
     * 邮编
     */
    @TableField("postal_code")
    private String postalCode;

    /**
     * 身份证审核状态 0.未提交 1。审核中 2。审核成功 3。 审核退回
     */
    @TableField("kyc_status")
    private Integer kycStatus;

    /**
     * 用户绑定的设备指纹
     */
    @TableField("fingerprint")
    private String fingerprint;

    /**
     * 用户地址
     */
    @TableField("wallet_address")
    private String walletAddress;

    /**
     * 用户头像地址
     */
    @TableField("portrait_address")
    private String portraitAddress;

    /**
     * 用户邀请码
     */
    @TableField("invitation_code")
    private String invitationCode;

    /**
     * 用户address
     */
    @TableField("address")
    private String address;

    /**
     * 用户自己输入的地址(用户自己输入的详细地址)
     */
    @TableField("user_address")
    private String userAddress;

    /**
     * 三方登录令牌
     */
    @TableField("third_token")
    private String thirdToken;

    /**
     * 首充标识 0-新人未首充 1-已首充
     */
    @TableField("first_deposit_flag")
    private Integer firstDepositFlag;

    /**
     * 三方登录令牌
     */
    @TableField("permissions")
    private String permissions;

    /**
     * 国家
     */
    @TableField("area")
    private String area;
}
