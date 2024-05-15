package com.ulla.modules.business.vo;

import java.math.BigDecimal;
import java.util.List;

import com.ulla.modules.binance.mo.QuotationProductMo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 撤单视图
 * @since 2023/3/22 13:20
 */
@Data
public class CloseOrderVo {

    @ApiModelProperty(value = "数据编号")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private String orderCode;

    @ApiModelProperty(value = "涨跌类型 0。买跌 1。买涨")
    private Integer openClose;

    @ApiModelProperty(value = "交易对")
    private String pairs;

    @ApiModelProperty(value = "下单时刻的价格")
    private String price;

    @ApiModelProperty(value = "结余")
    private BigDecimal surplusPrice;

    @ApiModelProperty(value = "下单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "下单时间")
    private Long orderTime;

    @ApiModelProperty(value = "结算区间结束时间")
    private Long tradingEndTime;

    @ApiModelProperty(value = "撤单时间")
    private Long updateTime;

    @ApiModelProperty(value = "行情组")
    private List<QuotationProductMo> quotationProductMos;

}
