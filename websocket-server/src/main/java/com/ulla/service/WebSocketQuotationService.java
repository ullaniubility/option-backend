package com.ulla.service;

import java.io.IOException;

import com.ulla.modules.binance.qo.BinanceQuotationQo;
import com.ulla.modules.binance.qo.OrderRankQo;
import com.ulla.modules.binance.qo.SettleQo;
import com.ulla.modules.business.qo.WebsocketTransactionCategoryQo;

public interface WebSocketQuotationService {

    void openQuotation(String userCode, BinanceQuotationQo binanceQuotationQo);

    void closeQuotation(String userCode);

    void queryHisQuotation(String userCode, BinanceQuotationQo binanceQuotationQo);

    void getSymbolList(String userCode, WebsocketTransactionCategoryQo websocketTransactionCategoryQo)
        throws IOException;

    void openKlineQuotation(String userCode, BinanceQuotationQo binanceQuotationQo);

    void queryHisKlineQuotation(String userCode, BinanceQuotationQo binanceQuotationQo);

    void orderCalculation(String userCode, SettleQo settleQo);

    void orderResult(String userCode, SettleQo settleQo);

    void updateOnlineCount(int onlineCount);

    void orderRank(String userCode, OrderRankQo orderRankQo);
}
