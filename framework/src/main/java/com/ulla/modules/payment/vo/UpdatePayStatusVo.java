package com.ulla.modules.payment.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdatePayStatusVo {

    @ApiModelProperty(value = "订单id")
    private Long id;

    @ApiModelProperty(value = "交易hash")
    private String txHash;

}
