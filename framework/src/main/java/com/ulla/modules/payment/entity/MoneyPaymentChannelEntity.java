package com.ulla.modules.payment.entity;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 公司收款链配置
 * @author zhuyongdong
 * @since 2023-05-06 18:02:21
 */
@Data
@TableName("money_payment_channel")
@ApiModel(value = "公司收款链配置")
public class MoneyPaymentChannelEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "展示名称")
    @ApiModelProperty(name = "展示名称")
    private String name;

    @NotNull(message = "币链不能为空")
    @ApiModelProperty(name = "币链")
    private String net;

    @NotNull(message = "币种符号不能为空")
    @ApiModelProperty(name = "币种符号")
    private String symbol;

    @NotNull(message = "收币地址不能为空")
    @ApiModelProperty(name = "收币地址")
    private String address;

    @NotNull(message = "代币合约地址不能为空")
    @ApiModelProperty(name = "代币合约地址")
    private String contractAddress;

    @ApiModelProperty(name = "币链图标地址")
    private String iconUrl;

}
