package com.ulla.binance.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ulla.binance.cache.Cache;
import com.ulla.binance.client.impl.WebsocketClientImpl;
import com.ulla.binance.client.utils.WebSocketConnectionBean;
import com.ulla.binance.common.constant.QuotationConstant;
import com.ulla.binance.common.utils.DateUtil;
import com.ulla.binance.common.utils.IdUtils;
import com.ulla.binance.common.utils.ResultUtil;
import com.ulla.binance.common.utils.StringUtils;
import com.ulla.binance.common.vo.ResultMessageVo;
import com.ulla.binance.mo.QuotationKLineProductMo;
import com.ulla.binance.mo.QuotationProductMo;
import com.ulla.binance.service.BinanceAsyncTask;
import com.ulla.binance.service.BinanceService;
import com.ulla.binance.vo.QuotationKLineCountVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/14 16:51
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BinanceServiceImpl implements BinanceService {

    final Cache cache;

    final BinanceAsyncTask binanceAsyncTask;

    private Map<String, WebSocketConnectionBean> connections = new HashMap<>();

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(50, 200, 10, TimeUnit.SECONDS,
        new LinkedBlockingDeque(1000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void closeTicker(String symbol, String interval) {
        if (connections.containsKey(symbol + "_" + interval)) {
            WebSocketConnectionBean bean = connections.get(symbol + "_" + interval);
            WebsocketClientImpl websocketClient = bean.getWebsocketClient();
            websocketClient.closeConnection(bean.getClientId());
            log.info("symbol:{} - tickerId：{}已关闭", symbol + "_" + interval, bean.getClientId());
        } else {
            log.info("symbol : {} does not exist!", symbol + "_" + interval);
        }
    }

    @Override
    public void closeAll() {
        if (!connections.isEmpty()) {
            log.info("Closing {} connections(s)", connections.size());
            Iterator<Map.Entry<String, WebSocketConnectionBean>> iter = connections.entrySet().iterator();
            while (iter.hasNext()) {
                WebSocketConnectionBean bean = iter.next().getValue();
                WebsocketClientImpl websocketClient = bean.getWebsocketClient();
                websocketClient.closeConnection(bean.getClientId());
                iter.remove();
            }
        }
        log.info("All connections are closed!");
    }

    @Override
    public void miniTicker(String symbol) {
        try {
            log.info("连接到接口，参数:{},准备连接websocket", symbol);
            WebSocketConnectionBean bean = new WebSocketConnectionBean();
            WebsocketClientImpl client = new WebsocketClientImpl();
            Integer connectionId = client.miniTickerStream(symbol, ((event) -> {
                try {
                    String quotation = (new String(event.getBytes(RemotingHelper.DEFAULT_CHARSET)));
                    cache.put(QuotationConstant.BINANCE_QUOTATION_ARBUSDT + symbol, quotation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            bean.setClientId(connectionId);
            bean.setWebsocketClient(client);
            connections.put(symbol, bean);
            log.info("streamName：{}正在运行", connectionId);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("已运行或异常");
        }
    }

    @Override
    public String getQuotation(String symbol) {
        try {
            if (StringUtils.isBlank(symbol)) {
                symbol = "ARBUSDT";
            }
            if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_ARBUSDT + symbol)) {
                return cache.get(QuotationConstant.BINANCE_QUOTATION_ARBUSDT + symbol).toString();
            } else {
                miniTicker(symbol);
                return getQuotation(symbol);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("资管获取报价异常");
            return null;
        }
    }

    @Component
    @Order(value = 1)
    public class StartRunnerOne implements CommandLineRunner {
        @Override
        public void run(String... args) throws Exception {
            log.info(">>>服务启动完成，执行行情初始化任务<<<<");
            klineTicker("BTCUSDT", "1s");
            klineTickerByInterval("BTCUSDT", "5m");
            klineTickerByInterval("BTCUSDT", "10m");
            klineTickerByInterval("BTCUSDT", "15m");
            klineTickerByInterval("BTCUSDT", "30m");
            klineTickerByInterval("BTCUSDT", "1h");
            klineTickerByInterval("BTCUSDT", "4h");
            klineTickerByInterval("BTCUSDT", "1d");
            klineTickerByInterval("BTCUSDT", "1w");
            klineTickerByInterval("BTCUSDT", "1M");
            miniTicker("ARBUSDT");
        }

    }

    @Override
    public ResultMessageVo startBySymbol(String symbol) {
        klineTicker(symbol, "1s");
        klineTickerByInterval(symbol, "5m");
        klineTickerByInterval(symbol, "10m");
        klineTickerByInterval(symbol, "15m");
        klineTickerByInterval(symbol, "30m");
        klineTickerByInterval(symbol, "1h");
        klineTickerByInterval(symbol, "4h");
        klineTickerByInterval(symbol, "1d");
        klineTickerByInterval(symbol, "1w");
        klineTickerByInterval(symbol, "1M");
        log.info(">>>{}服务启动完成，执行行情初始化任务<<<<", symbol);
        return ResultUtil.success();
    }

    @Override
    public ResultMessageVo closeBySymbol(String symbol) {
        closeTicker(symbol, "1s");
        closeTicker(symbol, "5m");
        closeTicker(symbol, "10m");
        closeTicker(symbol, "15m");
        closeTicker(symbol, "30m");
        closeTicker(symbol, "1h");
        closeTicker(symbol, "4h");
        closeTicker(symbol, "1d");
        closeTicker(symbol, "1w");
        closeTicker(symbol, "1M");
        log.info(">>>{}服务关闭完成<<<<", symbol);
        return ResultUtil.success();
    }

    @Override
    public void klineTicker(String symbol, String interval) {
        try {
            log.info("连接到接口，参数:{},准备连接websocket", symbol);
            WebSocketConnectionBean bean = new WebSocketConnectionBean();
            WebsocketClientImpl client = new WebsocketClientImpl();
            Integer connectionId = client.klineStream(symbol, interval, ((event) -> {
                try {
                    String msg = new String(event.getBytes(RemotingHelper.DEFAULT_CHARSET));
                    log.debug("1s行情消息内容{}", msg);
                    if (null != msg) {
                        binanceKLineQuotation(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            bean.setClientId(connectionId);
            bean.setWebsocketClient(client);
            connections.put(symbol + "_" + interval, bean);
            log.info("streamName：{}正在运行", connectionId);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("已运行或异常");
        }
    }

    @Override
    public void klineTickerByInterval(String symbol, String interval) {
        try {
            log.info("连接到接口，参数:{},准备连接websocket", symbol);
            WebSocketConnectionBean bean = new WebSocketConnectionBean();
            WebsocketClientImpl client = new WebsocketClientImpl();
            Integer connectionId = client.klineStream(symbol, interval, ((event) -> {
                try {
                    String msg = new String(event.getBytes(RemotingHelper.DEFAULT_CHARSET));
                    if (null != msg) {
                        Map<String, Object> jsonMap = JSON.parseObject(msg);
                        Map<String, Object> dataMap = (Map<String, Object>)jsonMap.get("k");
                        QuotationKLineProductMo mo = QuotationKLineProductMo.getInstance();
                        mo.setStreamTime(Long.valueOf(jsonMap.get("E").toString()));
                        mo.setSymbol(jsonMap.get("s").toString());
                        mo.setKStartTime(Long.valueOf(dataMap.get("t").toString()));
                        mo.setKEndTime(Long.valueOf(dataMap.get("T").toString()));
                        mo.setOpenPrice(dataMap.get("o").toString());
                        mo.setClosePrice(dataMap.get("c").toString());
                        mo.setHighPrice(dataMap.get("h").toString());
                        mo.setLowPrice(dataMap.get("l").toString());
                        mo.setTurnoverNum(dataMap.get("v").toString());
                        mo.setTurnoverAmount(Long.valueOf(dataMap.get("n").toString()));
                        mo.setCloseFlag((boolean)dataMap.get("x") ? 1 : 0);
                        mo.setTranLimit(dataMap.get("q").toString());
                        // 修正后的时间
                        Long correctTime = Long.valueOf(mo.getStreamTime().toString().substring(0, 10) + "000");
                        mo.setStreamTime(correctTime);

                        /*Message sendMsg = new Message("kline-" + mo.getSymbol(), interval,
                            JSONObject.toJSONString(mo).getBytes(RemotingHelper.DEFAULT_CHARSET));
                        SendResult sendResult = MqUtil.getProducer("binance").send(sendMsg);*/
                        // log.info("发送消息结果:" + sendResult);
                        String intervalType = interval.equals("1w") ? "7d" : interval.equals("1M") ? "1mon" : interval;
                        if (mo.getCloseFlag().intValue() == 1) {
                            binanceAsyncTask.binanceKLineByInterval(mo, intervalType);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            bean.setClientId(connectionId);
            bean.setWebsocketClient(client);
            connections.put(symbol + "_" + interval, bean);
            log.info("streamName：{}正在运行", connectionId);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("已运行或异常");
        }
    }

    private void binanceKLineQuotation(String quotation) {
        // Instant start = Instant.now();

        Map<String, Object> jsonMap = JSON.parseObject(quotation);
        Map<String, Object> dataMap = (Map<String, Object>)jsonMap.get("k");

        QuotationProductMo productMo = QuotationProductMo.getInstance();

        productMo.setStreamTime(Long.valueOf(jsonMap.get("E").toString()));
        productMo.setSymbol(jsonMap.get("s").toString());

        productMo.setKStartTime(Long.valueOf(dataMap.get("t").toString()));
        productMo.setKEndTime(Long.valueOf(dataMap.get("T").toString()));
        productMo.setOpenPrice(dataMap.get("o").toString());
        productMo.setClosePrice(dataMap.get("c").toString());
        productMo.setHighPrice(dataMap.get("h").toString());
        productMo.setLowPrice(dataMap.get("l").toString());
        productMo.setTurnoverNum(dataMap.get("v").toString());
        productMo.setTurnoverAmount(Long.valueOf(dataMap.get("n").toString()));
        productMo.setCloseFlag((boolean)dataMap.get("x") ? 1 : 0);
        productMo.setTranLimit(dataMap.get("q").toString());
        // 修正后的时间
        Long correctTime = Long.valueOf(productMo.getStreamTime().toString().substring(0, 10) + "000");
        // 修正延迟35秒后的时间
        Long delayTime = correctTime + 35000;
        productMo.setStreamTime(delayTime);
        productMo.setUuid(IdUtils.get12SimpleUUID());
        // 下单区间数据处理
        Long orderStartTime = DateUtil.getOnMinute(delayTime) - 30000;
        Long orderEndTime = orderStartTime + 59999;
        productMo.setOrderStartTime(orderStartTime);
        productMo.setOrderEndTime(orderEndTime);
        productMo.setOrderRangeId(getThisOrderRangeId(productMo.getSymbol(), orderStartTime));
        // 结算区间数据处理
        Long tradingStartTime = DateUtil.getTradingStartTime(delayTime);
        Long tradingEndTime = tradingStartTime + 29999;

        productMo.setTradingStartTime(tradingStartTime);
        productMo.setTradingEndTime(tradingEndTime);
        productMo.setTradingRangeId(getThisTradingRangeId(productMo.getSymbol(), tradingStartTime));

        // 缓存1S数据
        log.debug("缓存1S数据");
        cache.put(
            QuotationConstant.BINANCE_QUOTATION_CACHE_1S + productMo.getSymbol() + QuotationConstant.SPLIT + delayTime,
            productMo, 95000l, TimeUnit.MILLISECONDS);
        extractedOneSeconds(productMo);
        extractedFiveSeconds(productMo);

        try {
            /*// 1S 数据处理
            CompletableFuture.runAsync(() -> {
                log.debug("1S行情异步线程");
                
            }, executor).get();
            CompletableFuture<Void> future5s = CompletableFuture.runAsync(() -> {
                // 5S 数据处理
                
            }, executor);*/
            CompletableFuture<Void> future10s = CompletableFuture.runAsync(() -> {
                // 10S 数据处理
                extractedTenSeconds(productMo);
            }, executor);
            CompletableFuture<Void> future15s = CompletableFuture.runAsync(() -> {
                // 15S 数据处理
                extractedFifteenSeconds(productMo);
            }, executor);
            CompletableFuture<Void> future30s = CompletableFuture.runAsync(() -> {
                // 30S 数据处理
                extractedThirtySeconds(productMo);
            }, executor);
            CompletableFuture<Void> future1m = CompletableFuture.runAsync(() -> {
                // 1M 数据处理
                extractedOneMinute(productMo);
            }, executor);

            CompletableFuture<Void> allOf = CompletableFuture.allOf(future10s, future15s, future30s, future1m);
            allOf.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        log.info("K线数据处理耗时 {} 毫秒", timeElapsed);*/
    }

    /**
     * 存储1SK线
     * 
     * @param productMo
     */
    private void extractedOneSeconds(QuotationProductMo productMo) {
        log.debug("1s行情消息处理");
        // 数据处理逻辑
        if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_DATA_1S + productMo.getSymbol())) {
            List<QuotationProductMo> listQuotation = (List<QuotationProductMo>)cache
                .get(QuotationConstant.BINANCE_QUOTATION_DATA_1S + productMo.getSymbol());
            log.debug("{}1s行情listQuotation:{}", productMo.getSymbol(), listQuotation.toString());
            listQuotation.add(productMo);
            log.debug("{}1s行情目前条数:{}", productMo.getSymbol(), listQuotation.size());
            if (listQuotation.size() > 15) {
                log.debug("{}1s行情消息批处理，条数:{}", productMo.getSymbol(), listQuotation.size());
                binanceAsyncTask.binanceKQuotation(productMo.getSymbol(), listQuotation);
                cache.remove(QuotationConstant.BINANCE_QUOTATION_DATA_1S + productMo.getSymbol());
            } else {
                log.debug("{}新增1s行情消息，目前条数:{}", productMo.getSymbol(), listQuotation.size());
                cache.put(QuotationConstant.BINANCE_QUOTATION_DATA_1S + productMo.getSymbol(), listQuotation, 50000l,
                    TimeUnit.MILLISECONDS);
            }
        } else {
            log.debug("{}没有1s行情，1s行情消息处理，新数据集合", productMo.getSymbol());
            List<QuotationProductMo> listQuotation = Lists.newArrayList();
            listQuotation.add(productMo);
            cache.put(QuotationConstant.BINANCE_QUOTATION_DATA_1S + productMo.getSymbol(), listQuotation, 40000l,
                TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 存储5SK线
     * 
     * @param productMo
     */
    private void extractedFiveSeconds(QuotationProductMo productMo) {
        QuotationKLineProductMo mo = QuotationKLineProductMo.getInstance();
        BeanUtils.copyProperties(productMo, mo);
        // 当前时间
        Long time = mo.getStreamTime();
        // 5秒间隔开始时间戳
        Long startTime = DateUtil.getOnFiveSeconds(time);
        // 5秒间隔结束时间戳
        Long endTime = startTime + 4000;

        mo.setKStartTime(startTime);
        mo.setKEndTime(endTime);
        if (!(time.compareTo(endTime) == 0)) {
            mo.setCloseFlag(0);
        }
        // 数据计算
        if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(QuotationConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                vo = tempCount(mo, endTime);
                cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol(), vo, 8000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, QuotationConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol());
            }
        } else {
            QuotationKLineCountVo vo = tempCount(mo, endTime);
            cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol(), vo, 8000l, TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isFiveSecondsFlag(time)) {
            // 数据处理逻辑
            if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol())) {
                List<QuotationKLineProductMo> listQuotation = (List<QuotationKLineProductMo>)cache
                    .get(QuotationConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol());
                listQuotation.add(mo);
                if (listQuotation.size() > 5) {
                    binanceAsyncTask.binanceKLine5SQuotation(listQuotation);
                    cache.remove(QuotationConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol());
                } else {
                    cache.put(QuotationConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol(), listQuotation, 50000l,
                        TimeUnit.MILLISECONDS);
                }
            } else {
                List<QuotationKLineProductMo> listQuotation = Lists.newArrayList();
                listQuotation.add(mo);
                cache.put(QuotationConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol(), listQuotation, 50000l,
                    TimeUnit.MILLISECONDS);
            }
            // 缓存5S数据
            cache.put(QuotationConstant.BINANCE_QUOTATION_CACHE_5S + mo.getSymbol() + QuotationConstant.SPLIT + time,
                mo, 50000l, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 存储10SK线
     *
     * @param productMo
     */
    private void extractedTenSeconds(QuotationProductMo productMo) {
        QuotationKLineProductMo mo = QuotationKLineProductMo.getInstance();
        BeanUtils.copyProperties(productMo, mo);
        // 当前时间
        Long time = mo.getStreamTime();
        // 10秒间隔开始时间戳
        Long startTime = DateUtil.getOnTenSeconds(time);
        // 10秒间隔结束时间戳
        Long endTime = startTime + 9000;
        mo.setKStartTime(startTime);
        mo.setKEndTime(endTime);
        if (!(mo.getStreamTime().compareTo(endTime) == 0)) {
            mo.setCloseFlag(0);
        }

        // 数据计算
        if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(QuotationConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol(), tempCount(mo, endTime), 13000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, QuotationConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol());
            }
        } else {
            cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol(), tempCount(mo, endTime), 13000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isTenSecondsFlag(time)) {
            // 缓存10S数据
            cache.put(QuotationConstant.BINANCE_QUOTATION_CACHE_10S + mo.getSymbol() + QuotationConstant.SPLIT + time,
                mo, 40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceAsyncTask.binanceKLineByInterval(mo, "10s");
            }
        }
    }

    /**
     * 存储15SK线
     *
     * @param productMo
     */
    private void extractedFifteenSeconds(QuotationProductMo productMo) {
        QuotationKLineProductMo mo = QuotationKLineProductMo.getInstance();
        BeanUtils.copyProperties(productMo, mo);
        // 当前时间
        Long time = mo.getStreamTime();
        // 15秒间隔开始时间戳
        Long startTime = DateUtil.getOnFifteenSeconds(time);
        // 15秒间隔结束时间戳
        Long endTime = startTime + 14000;
        mo.setKStartTime(startTime);
        mo.setKEndTime(endTime);
        if (!(mo.getStreamTime().compareTo(endTime) == 0)) {
            mo.setCloseFlag(0);
        }

        // 数据计算
        if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(QuotationConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol(), tempCount(mo, endTime), 18000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, QuotationConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol());
            }
        } else {
            cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol(), tempCount(mo, endTime), 18000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isFifteenSecondsFlag(time)) {
            // 缓存15S数据
            cache.put(QuotationConstant.BINANCE_QUOTATION_CACHE_15S + mo.getSymbol() + QuotationConstant.SPLIT + time,
                mo, 40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceAsyncTask.binanceKLineByInterval(mo, "15s");
            }
        }
    }

    /**
     * 存储30SK线
     *
     * @param productMo
     */
    private void extractedThirtySeconds(QuotationProductMo productMo) {
        QuotationKLineProductMo mo = QuotationKLineProductMo.getInstance();
        BeanUtils.copyProperties(productMo, mo);
        // 当前时间
        Long time = mo.getStreamTime();
        // 30秒间隔开始时间戳
        Long startTime = DateUtil.getOnThirtySeconds(time);
        // 30秒间隔结束时间戳
        Long endTime = startTime + 29000;
        mo.setKStartTime(startTime);
        mo.setKEndTime(endTime);
        if (!(mo.getStreamTime().compareTo(endTime) == 0)) {
            mo.setCloseFlag(0);
        }

        // 数据计算
        if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(QuotationConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol(), tempCount(mo, endTime), 34000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, QuotationConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol());
            }
        } else {
            cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol(), tempCount(mo, endTime), 34000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isThirtySecondsFlag(time)) {
            // 缓存30S数据
            cache.put(QuotationConstant.BINANCE_QUOTATION_CACHE_30S + mo.getSymbol() + QuotationConstant.SPLIT + time,
                mo, 40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceAsyncTask.binanceKLineByInterval(mo, "30s");
            }
        }
    }

    /**
     * 存储1MK线
     *
     * @param productMo
     */
    private void extractedOneMinute(QuotationProductMo productMo) {
        QuotationKLineProductMo mo = QuotationKLineProductMo.getInstance();
        BeanUtils.copyProperties(productMo, mo);
        // 当前时间
        Long time = mo.getStreamTime();
        // 1分钟间隔开始时间戳
        Long startTime = DateUtil.getOnMinute(time);
        // 1分钟间隔结束时间戳
        Long endTime = startTime + 59000;
        mo.setKStartTime(startTime);
        mo.setKEndTime(endTime);
        if (!(mo.getStreamTime().compareTo(endTime) == 0)) {
            mo.setCloseFlag(0);
        }

        // 数据计算
        if (cache.hasKey(QuotationConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(QuotationConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol(), tempCount(mo, endTime), 64000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, QuotationConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol());
            }
        } else {
            cache.put(QuotationConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol(), tempCount(mo, endTime), 64000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isOneMinuteFlag(time)) {
            // 缓存1M数据
            cache.put(QuotationConstant.BINANCE_QUOTATION_CACHE_1M + mo.getSymbol() + QuotationConstant.SPLIT + time,
                mo, 40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceAsyncTask.binanceKLineByInterval(mo, "1m");
            }
        }
    }

    /**
     * 保存记录
     * 
     * @param mo
     * @param vo
     */
    private void keepRecords(QuotationKLineProductMo mo, QuotationKLineCountVo vo, String cacheKey) {
        BigDecimal highPrice = new BigDecimal(mo.getHighPrice());
        BigDecimal lowPrice = new BigDecimal(mo.getLowPrice());
        BigDecimal turnoverNum = new BigDecimal(mo.getTurnoverNum());
        Long turnoverAmount = mo.getTurnoverAmount();
        BigDecimal tranLimit = new BigDecimal(mo.getTranLimit());

        vo.setHighPrice(
            highPrice.compareTo(new BigDecimal(vo.getHighPrice())) == 1 ? highPrice.toString() : vo.getHighPrice());
        vo.setLowPrice(
            lowPrice.compareTo(new BigDecimal(vo.getLowPrice())) == 1 ? vo.getLowPrice() : lowPrice.toString());
        if (turnoverNum.compareTo(new BigDecimal("0")) == 0) {
            vo.setTurnoverNum(vo.getTurnoverNum());
        } else {
            vo.setTurnoverNum(turnoverNum.add(new BigDecimal(vo.getTurnoverNum())).toString());
        }

        vo.setTurnoverAmount(turnoverAmount.longValue() + vo.getTurnoverAmount().longValue());
        if (tranLimit.compareTo(new BigDecimal("0")) == 0) {
            vo.setTranLimit(vo.getTranLimit());
        } else {
            vo.setTranLimit(tranLimit.add(new BigDecimal(vo.getTranLimit())).toString());
        }
        cache.put(cacheKey, vo, 64000l, TimeUnit.MILLISECONDS);

        mo.setOpenPrice(vo.getOpenPrice());
        mo.setHighPrice(vo.getHighPrice());
        mo.setLowPrice(vo.getLowPrice());
        mo.setTurnoverNum(vo.getTurnoverNum());
        mo.setTurnoverAmount(vo.getTurnoverAmount());
        mo.setTranLimit(vo.getTranLimit());
    }

    /**
     * 数据计算
     * 
     * @param mo
     * @param endTime
     * @return
     */
    private QuotationKLineCountVo tempCount(QuotationKLineProductMo mo, Long endTime) {
        QuotationKLineCountVo temp = QuotationKLineCountVo.getInstance();
        temp.setClosePrice(mo.getClosePrice());
        temp.setOpenPrice(mo.getOpenPrice());
        temp.setHighPrice(mo.getHighPrice());
        temp.setLowPrice(mo.getLowPrice());
        temp.setTurnoverAmount(mo.getTurnoverAmount());
        temp.setTurnoverNum(mo.getTurnoverNum());
        temp.setTranLimit(mo.getTranLimit());
        temp.setEndTime(endTime);
        return temp;
    }

    /**
     * 结算区间编号
     *
     * @param symbol
     * @param tradingStartTime
     * @return
     */
    public String getThisTradingRangeId(String symbol, Long tradingStartTime) {

        if (cache.hasKey(QuotationConstant.TRADING_RANGE_ID + symbol + QuotationConstant.SPLIT + tradingStartTime)) {
            return cache.get(QuotationConstant.TRADING_RANGE_ID + symbol + QuotationConstant.SPLIT + tradingStartTime)
                .toString();
        } else {
            String tradingRangeId = IdUtils.get12SimpleUUID();
            cache.put(QuotationConstant.TRADING_RANGE_ID + symbol + QuotationConstant.SPLIT + tradingStartTime,
                tradingRangeId, 40000l, TimeUnit.MILLISECONDS);
            return tradingRangeId;
        }
    }

    /**
     * 下单区间编号
     *
     * @param symbol
     * @param orderStartTime
     * @return
     */
    public String getThisOrderRangeId(String symbol, Long orderStartTime) {
        if (cache.hasKey(QuotationConstant.ORDER_RANGE_ID + symbol + QuotationConstant.SPLIT + orderStartTime)) {
            return cache.get(QuotationConstant.ORDER_RANGE_ID + symbol + QuotationConstant.SPLIT + orderStartTime)
                .toString();
        } else {
            String orderRangeId = IdUtils.get12SimpleUUID();
            cache.put(QuotationConstant.ORDER_RANGE_ID + symbol + QuotationConstant.SPLIT + orderStartTime,
                orderRangeId, 70000l, TimeUnit.MILLISECONDS);
            return orderRangeId;
        }
    }

    // TODO 补数据逻辑，暂时不需要，
    /* public List<QuotationProductMo> complementDate(QuotationProductMo originalProduct) {
        List<QuotationProductMo> batchList = Lists.newArrayList();
        if (cache.hasKey(CacheConstant.MINI_TICKER_TIME + originalProduct.getSymbol())) {
            Long cacheTime = Long.valueOf(cache.get(CacheConstant.MINI_TICKER_TIME + originalProduct.getSymbol()) + "");
            int batchNum = ((originalProduct.getStreamTime().intValue() - cacheTime.intValue()) / 1000) - 1;
            for (int i = 1; i <= batchNum; i++) {
                log.info("{} 补推送数据:{} ------------------------------",
                    originalProduct.getStreamName() + "-" + originalProduct.getSymbol(), i);
                QuotationProductMo tempProduct = QuotationProductMo.getInstance();
                BeanUtils.copyProperties(originalProduct, tempProduct);
                tempProduct.setUuid(IdUtils.get12SimpleUUID());
                tempProduct.setStreamTime(cacheTime.longValue() + 1000 * i);
                // 结算区间开始时间
                Long tempTradingStartTime = DateUtil.getHalfMinute(tempProduct.getStreamTime());
    
                tempProduct.setTradingRangeId(getThisTradingRangeId(tempProduct.getStreamName(), tempTradingStartTime));
                // 下单区间开始时间
                Long tempOrderStartTime = DateUtil.getOnMinute(tempProduct.getStreamTime()) - 30000;
                tempProduct.setOrderRangeId(getThisOrderRangeId(tempProduct.getStreamName(), tempOrderStartTime));
                batchList.add(tempProduct);
            }
        }
        cache.put(CacheConstant.MINI_TICKER_TIME + originalProduct.getSymbol(), originalProduct.getStreamTime(), 20000l,
            TimeUnit.MILLISECONDS);
        return batchList;
    }*/

}
