package com.ulla.modules.business.vo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

@Data
public class SymbolNameVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 币种符号
     */
    private String symbol;

    /**
     * 类型(ETH BTC TRX MATIC HECO BSC)
     */
    private String net;

    private String symbolNames;

}
