package com.ulla.modules.assets.mo;

import com.baomidou.mybatisplus.annotation.*;
import com.ulla.mybatis.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 用户钱包地址表
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("biz_address")
@ApiModel(value = "Address对象", description = "用户钱包地址表")
public class AddressMo  extends BaseEntity {

    @ApiModelProperty("网络")
    @TableField("net")
    private String net;

    @ApiModelProperty("地址值")
    @TableField("address")
    private String address;

    @ApiModelProperty("绑定的用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("状态 0-未绑定 1，已绑定 2，已失效")
    @TableField("state")
    private Integer state;

    @TableField("scan_flag")
    private Integer scanFlag;

    /**
     * 地址类型：1-法币充值地址 2-虚拟货币充值地址
     */
    @TableField("`type`")
    private Integer type;


}
