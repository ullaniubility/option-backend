package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.util.List;

import com.ulla.modules.binance.mo.QuotationProductMo;
import com.ulla.modules.business.mo.OrderMo;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/3/27 13:16
 */
@Data
public class OrderResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    List<OrderMo> orderMos;

    List<QuotationProductMo> quotationProductMos;

}
