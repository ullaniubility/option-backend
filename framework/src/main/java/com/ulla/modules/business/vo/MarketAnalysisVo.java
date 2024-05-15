package com.ulla.modules.business.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MarketAnalysisVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long uid;

    /**
     * 月赢交易比
     */
    private BigDecimal winPercent;
    /**
     * 月交易总金额
     */
    private BigDecimal transactionTotal;

    /**
     * 月交易总数
     */
    private Integer monthTotal;

    /**
     * 交易对名称和交易量百分比
     */
    private List<MarketAnalysis> marketAnalysisList;

    /**
     * 日交易时间和日交易金额
     */
    private List<Transaction> transactions;

    @Getter
    @Setter
    public static class MarketAnalysis implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        /**
         * 交易对名称
         */
        @ApiModelProperty("交易对名称")
        private String name;

        /**
         * 每对交易对占该月交易对的百分比，取前三，后门的统称其他
         */
        private BigDecimal percent;

        private Integer pairCount;

    }

    @Getter
    @Setter
    public static class Transaction implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 日交易时间
         */
        private String priceTime;

        /**
         * 日交易金额
         */
        private BigDecimal priceTotal;

    }
}
