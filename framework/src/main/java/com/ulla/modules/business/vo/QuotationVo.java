package com.ulla.modules.business.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class QuotationVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 行情时间
     */
    private Long quotationTime;

    /**
     * 行情价格
     */
    private String quotationPrice;
}
