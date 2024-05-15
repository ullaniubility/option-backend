package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 历史订单视图
 * @since 2023/3/31 10:12
 */
@Data
public class OrderHistoryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据编号")
    private Long id;

    @ApiModelProperty(value = "日期")
    private String endDate;

    @ApiModelProperty(value = "日交易笔数")
    private Integer orderCountByDate;

    @ApiModelProperty(value = "日盈亏")
    private BigDecimal totalByDate;

    @ApiModelProperty(value = "交易对")
    private String pairs;

    @ApiModelProperty(value = "分组结算价格")
    private String endPrice;

    @ApiModelProperty(value = "分组结算时间")
    private String endTime;

    @ApiModelProperty(value = "分组结算时间戳")
    private Long endTimeStamp;

    @ApiModelProperty(value = "分组交易笔数")
    private Integer orderCountByTime;

    @ApiModelProperty(value = "分组盈亏")
    private BigDecimal totalByTime;

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

    @ApiModelProperty(value = "订单状态")
    private Integer status;

}
