package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 历史订单返回结构视图
 * @since 2023/3/31 10:12
 */
@Data
public class OrderHistoryResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期")
    private String endDate;

    @ApiModelProperty(value = "日期时间戳")
    private Long endDateStamp;

    @ApiModelProperty(value = "日交易笔数")
    private Integer orderCountByDate;

    @ApiModelProperty(value = "日盈亏")
    private BigDecimal totalByDate;

    @ApiModelProperty(value = "订单列表")
    private List<OrderHistoryGroupTimeVo> listGroupTime;

}
