package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class RangeOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 下单时单价
     */
    private BigDecimal price;

    /**
     * 下单时间
     */
    private Long orderChildTime;

    /**
     * 单次下单金额
     */
    private BigDecimal orderAmount;

    /**
     * 买涨还是买跌0跌1涨
     */
    private Integer openClose;

    private String pairsChild;

    private List<QuotationVo> quotationVos;
}
