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
public class OrderHistoryGroupTimeVo implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "订单列表")
    private List<OrderHistoryResultListVo> listOrder;

}
