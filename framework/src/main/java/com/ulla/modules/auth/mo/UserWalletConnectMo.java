package com.ulla.modules.auth.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;

/**
 * @Description 用户WalletConnect钱包地址
 * @author zhuyongdong
 * @since 2023-04-25 15:07:05
 */
@Data
@TableName("biz_user_wallet_connect")
public class UserWalletConnectMo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField
    private Long uid;

    /**
     * 用户openId
     */
    @TableField("open_id")
    private String openId;

    /**
     * WalletConnect钱包地址
     */
    @TableField("address")
    private String address;

    /**
     * 链名称
     */
    @TableField("net")
    private String net;

    /**
     * 链编号
     */
    @TableField("net_id")
    private String netId;

}
