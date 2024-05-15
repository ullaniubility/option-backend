package com.ulla.binance.service;

import java.util.List;

import com.ulla.binance.mo.QuotationKLineProductMo;
import com.ulla.binance.mo.QuotationProductMo;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/22 10:14
 */
public interface BinanceAsyncTask {

    void binanceKQuotation(String symbol, List<QuotationProductMo> productMo);

    void binanceKLine5SQuotation(List<QuotationKLineProductMo> listQuotation);

    void binanceKLineByInterval(QuotationKLineProductMo mo, String intervalType);
}
