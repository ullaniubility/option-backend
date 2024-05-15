package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.ulla.modules.binance.mo.QuotationProductMo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 订单详情视图
 * @author zhuyongdong
 * @since 2023-04-01 11:48:18
 */
@Data
public class OrderDetailVo {

    @ApiModelProperty(value = "结算价格")
    private String tradingPrice;

    @ApiModelProperty(value = "结算时间")
    private String tradingTime;

    @ApiModelProperty(value = "结算时间戳")
    private Long tradingTimeStamp;

    @ApiModelProperty(value = "盈利")
    private BigDecimal profitPrice;

    @ApiModelProperty(value = "盈利")
    private BigDecimal totalOrderAmount;

    @ApiModelProperty(value = "订单组")
    private List<OrderVo> orderVos;

    @ApiModelProperty(value = "行情组")
    private List<QuotationProductMo> quotationProductMos;

    @Data
    public static class OrderVo implements Serializable {

        private static final long serialVersionUID = 1816357809660916086L;

        @ApiModelProperty(value = "涨跌类型 0。买跌 1。买涨")
        private Integer openClose;

        @ApiModelProperty(value = "下单时刻的价格")
        private String price;

        @ApiModelProperty(value = "下单时间")
        private String orderTime;

        @ApiModelProperty(value = "下单时间戳")
        private Long orderTimeStamp;

        @ApiModelProperty(value = "下单金额")
        private BigDecimal orderAmount;

        @ApiModelProperty(value = "盈利金额--单笔")
        private BigDecimal benefit;

        @ApiModelProperty(value = "撤单时间戳")
        private Long updateTime;

    }

}
