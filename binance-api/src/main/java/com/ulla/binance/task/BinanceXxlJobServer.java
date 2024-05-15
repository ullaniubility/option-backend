package com.ulla.binance.task;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.binance.cache.Cache;
import com.ulla.binance.common.constant.CommonConstant;
import com.ulla.binance.common.constant.QuotationConstant;
import com.ulla.binance.common.utils.DateUtil;
import com.ulla.binance.mapper.QuotationMapper;
import com.ulla.binance.mo.TransactionCategoryChildMo;
import com.ulla.binance.service.BinanceService;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 币安行情监测
 * @since 2023/2/23 11:41
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BinanceXxlJobServer {

    final Cache cache;

    final QuotationMapper quotationMapper;

    final BinanceService binanceService;

    /**
     * 行情检测
     */
    @XxlJob("detectionQuotation")
    public void detectionQuotation() {
        try {
            if (cache.hasKey(CommonConstant.SYMBOL_LIST)) {
                Long beforeTime = DateUtil.getReviseDate13line(null) - 38000;
                Gson gson = new Gson();
                Type type = new TypeToken<List<TransactionCategoryChildMo>>() {}.getType();
                List<TransactionCategoryChildMo> listSymbol =
                    gson.fromJson(cache.get(CommonConstant.SYMBOL_LIST).toString(), type);

                listSymbol.stream().forEach(symbol -> {
                    if (!cache.hasKey(QuotationConstant.BINANCE_QUOTATION_CACHE_1S + symbol.getChildName().toUpperCase()
                        + QuotationConstant.SPLIT + beforeTime)) {
                        if (!cache.hasKey(QuotationConstant.BINANCE_QUOTATION_CACHE_1S
                            + symbol.getChildName().toUpperCase() + QuotationConstant.SPLIT + (beforeTime - 1000))) {
                            if (!cache.hasKey(
                                QuotationConstant.BINANCE_QUOTATION_CACHE_1S + symbol.getChildName().toUpperCase()
                                    + QuotationConstant.SPLIT + (beforeTime - 2000))) {
                                log.info(">>>>>>>>>>>>>>>>重新连接{}行情！<<<<<<<<<<<<<<<<<<", symbol.getChildName());
                                binanceService.closeBySymbol(symbol.getChildName().toUpperCase());
                                binanceService.startBySymbol(symbol.getChildName().toUpperCase());
                            }
                        }
                    }
                });
            } else {
                log.error(">>>>>>>>>>>>>>>>没有开启的交易对或无交易对列表缓存！<<<<<<<<<<<<<<<<<<");
            }
        } catch (Exception e) {
            log.info("行情检测异常");
            e.printStackTrace();
        }
    }

}
