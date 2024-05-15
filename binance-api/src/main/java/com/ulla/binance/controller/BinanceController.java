package com.ulla.binance.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.binance.client.utils.WebSocketConnectionBean;
import com.ulla.binance.common.utils.ResultUtil;
import com.ulla.binance.common.vo.ResultMessageVo;
import com.ulla.binance.service.BinanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 币安API
 * @since 2023/2/9 17:08
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/binance")
public class BinanceController {

    private Map<String, WebSocketConnectionBean> connections = new HashMap<>();

    final BinanceService binanceService;

    /**
     * 通用币种行情报价
     * 
     * @param symbol
     */
    @GetMapping("/miniTicker")
    public void miniTicker(String symbol) {
        try {
            binanceService.miniTicker(symbol);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            log.info("已运行或异常");
        }
    }

    /**
     * 通用获取币种行情报价
     *
     * @param symbol
     */
    @GetMapping("/getQuotation")
    public String getQuotation(String symbol) {
        return binanceService.getQuotation(symbol);
    }

    /**
     * 添加币种K线
     *
     * @param symbol
     */
    @GetMapping("/klineTicker")
    public void klineTicker(String symbol, String interval) {
        try {
            if (interval.equals("1s")) {
                binanceService.klineTicker(symbol, interval);
            } else {
                binanceService.klineTickerByInterval(symbol, interval);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            log.info("已运行或异常");
        }
    }

    /**
     * 关闭币种行情
     * 
     * @param symbol
     */
    @GetMapping("/closeTickerBySymbol")
    public void closeTicker(String symbol, String interval) {
        binanceService.closeTicker(symbol, interval);
    }

    /**
     * 关闭所有币种行情
     */
    @GetMapping("/closeAll")
    public void closeAll() {
        binanceService.closeAll();
        log.info("All connections are closed!");
    }

    /**
     * 开启币种行情
     *
     * @param symbol
     */
    @GetMapping("/startBySymbol")
    public ResultMessageVo startBySymbol(String symbol) {
        binanceService.klineTicker(symbol, "1s");
        binanceService.klineTickerByInterval(symbol, "5m");
        binanceService.klineTickerByInterval(symbol, "10m");
        binanceService.klineTickerByInterval(symbol, "15m");
        binanceService.klineTickerByInterval(symbol, "30m");
        binanceService.klineTickerByInterval(symbol, "1h");
        binanceService.klineTickerByInterval(symbol, "4h");
        binanceService.klineTickerByInterval(symbol, "1d");
        binanceService.klineTickerByInterval(symbol, "1w");
        binanceService.klineTickerByInterval(symbol, "1M");
        return ResultUtil.success();
    }

    /**
     * 关闭币种行情
     *
     * @param symbol
     */
    @GetMapping("/closeBySymbol")
    public ResultMessageVo closeBySymbol(String symbol) {
        return binanceService.closeBySymbol(symbol);
    }

}
