package com.ulla.modules.payment.entity;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 公司测试用配置
 * @author zhuyongdong
 * @since 2023-05-12 11:33:00
 */
@Data
@TableName("money_test_channel")
@ApiModel(value = "公司测试用配置")
public class MoneyTestChannelEntity extends BaseEntity {

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

    @ApiModelProperty(name = "启用开关 0否 1是")
    private Integer offFlag;

}
