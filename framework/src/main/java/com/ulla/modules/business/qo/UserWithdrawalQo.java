package com.ulla.modules.business.qo;

import java.math.BigDecimal;

import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MoneyDeposit出金对象", description = "")
public class UserWithdrawalQo extends BaseEntity {

    @ApiModelProperty(value = "图标链接")
    private String logoUrl;

    @ApiModelProperty(value = "币链(实际到账数字币有值)")
    private String net;

    @ApiModelProperty(value = "币种符号(实际到账数字币有值)")
    private String symbol;

    @ApiModelProperty(value = "出金 - 货币单位")
    private String depositMonetaryUnit;

    @ApiModelProperty(value = "出金金额")
    private BigDecimal depositMonetaryAmount;

    @ApiModelProperty(value = "收币地址")
    private String address;

}
