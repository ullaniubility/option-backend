package com.ulla.binance.service;

import com.ulla.binance.common.vo.ResultMessageVo;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/14 16:51
 */
public interface BinanceService {

    void klineTicker(String symbol, String interval);

    void klineTickerByInterval(String symbol, String interval);

    void closeTicker(String symbol, String interval);

    void closeAll();

    void miniTicker(String symbol);

    String getQuotation(String symbol);

    ResultMessageVo startBySymbol(String symbol);

    ResultMessageVo closeBySymbol(String symbol);
}
