package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 订单结算倒计时
 * @author zhuyongdong
 * @since 2023-03-14 16:17:11
 */
@ApiModel(value = "订单结算倒计时")
@Data
public class UserOrderCalculationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "下单即损耗参数")
    private BigDecimal lossRatio;

    @ApiModelProperty(value = "下单最小损耗参数")
    private BigDecimal withdrawal;

    @ApiModelProperty(value = "系统当前时间")
    private Long streamTime;

    private List<OrderCalculationVo> orderList;

}
