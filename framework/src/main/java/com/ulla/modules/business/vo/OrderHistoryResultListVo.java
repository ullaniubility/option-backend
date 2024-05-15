package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 历史订单返回结构视图
 * @since 2023/3/31 10:12
 */
@Data
public class OrderHistoryResultListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据编号")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private String orderCode;

    @ApiModelProperty(value = "涨跌类型 0。买跌 1。买涨")
    private Integer openClose;

    @ApiModelProperty(value = "下单价格")
    private BigDecimal price;

    @ApiModelProperty(value = "下单时间")
    private String orderTime;

    @ApiModelProperty(value = "下单时间戳")
    private Long orderTimeStamp;

    @ApiModelProperty(value = "下单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "单笔盈利")
    private BigDecimal benefit;

}
