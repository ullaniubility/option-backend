package com.ulla.task;

import static com.ulla.constant.BinanceConstant.*;
import static com.ulla.constant.BinanceSymbolConstant.*;
import static com.ulla.constant.BinanceSymbolConstant.SPLIT;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ulla.cache.Cache;
import com.ulla.common.enums.WebsocketDateEnum;
import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.SourceEn;
import com.ulla.common.utils.StringUtils;
import com.ulla.modules.binance.mapper.QuotationKLineProductMapper;
import com.ulla.modules.binance.mapper.QuotationMapper;
import com.ulla.service.WebSocketServer;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 行情推送
 * @since 2023/2/23 11:41
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuotationXxlJobServer {

    final Cache cache;

    final QuotationMapper quotationMapper;

    final QuotationKLineProductMapper quotationKLineProductMapper;

    ExecutorService service = new ThreadPoolExecutor(200, 1000, 10, TimeUnit.SECONDS, new SynchronousQueue<>());

    /**
     * 行情推送
     */
    @XxlJob("quotation")
    public void quotation() {
        try {
            List<String> keys = cache.keys(QUOTATION_PREFIX + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[1];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    String jsonString = new String();
                    Gson gson = new Gson();
                    if (cache.hasKey(BINANCE_QUOTATION_CACHE_1S + symbol + SPLIT + dataTime)) {
                        jsonString = gson.toJson(cache.get(BINANCE_QUOTATION_CACHE_1S + symbol + SPLIT + dataTime));
                    } else {
                        jsonString =
                            gson.toJson(quotationMapper.selectQuotationByStreamTime(symbol.toLowerCase(), dataTime));
                    }
                    if (StringUtils.isBlank(jsonString) || "".equals(jsonString) || null == jsonString
                        || "null".equals(jsonString)) {
                        return;
                    }
                    String finalJsonString = jsonString;
                    keys.stream().filter(v -> v.contains(symbol)).forEach(key -> {
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            String userCode = key.split(":")[2];
                            if (!WebSocketServer.queryUserIsOnline(userCode)) {
                                log.debug("userCode:{}", userCode);
                                log.debug("key:{}", key);
                                cache.remove(key);
                            } else {
                                sendQuotationByUserCode(userCode, finalJsonString, WebsocketDateEnum.ACTUAL);
                            }
                        }, service);
                        try {
                            future.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 5S K线推送
     */
    @XxlJob("klineFiveSeconds")
    public void klineFiveSeconds() {
        if (DateUtil.isFiveSecondsFlag(DateUtil.getDate13line())) {
            try {
                List<String> keys = cache.keys(KLINE_PREFIX_5S + "*");
                if (keys.size() > 0) {
                    Long dataTime = DateUtil.getReviseDate13line(null);
                    // 取交易对并去重
                    List<String> symbols = Lists.newArrayList();
                    List<String> finalSymbols = symbols;
                    keys.stream().forEach(key -> {
                        String symbol = key.split(":")[2];
                        finalSymbols.add(symbol);
                    });
                    symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                    // 取交易对的用户
                    symbols.stream().forEach(symbol -> {
                        String jsonString = new String();
                        Gson gson = new Gson();
                        if (cache.hasKey(BINANCE_QUOTATION_CACHE_5S + symbol + SPLIT + dataTime)) {
                            jsonString = gson.toJson(cache.get(BINANCE_QUOTATION_CACHE_5S + symbol + SPLIT + dataTime));
                        } else {
                            jsonString = gson
                                .toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "5s"));
                        }
                        quotationExist(keys, symbol, jsonString);
                    });
                }
            } catch (Exception e) {
                log.info("停止推送");
                e.printStackTrace();
            }
        }
    }

    /**
     * 10S K线推送
     */
    @XxlJob("klineTenSeconds")
    public void klineTenSeconds() {
        if (DateUtil.isTenSecondsFlag(DateUtil.getDate13line())) {
            try {
                List<String> keys = cache.keys(KLINE_PREFIX_10S + "*");
                if (keys.size() > 0) {
                    Long dataTime = DateUtil.getReviseDate13line(null);
                    // 取交易对并去重
                    List<String> symbols = Lists.newArrayList();
                    List<String> finalSymbols = symbols;
                    keys.stream().forEach(key -> {
                        String symbol = key.split(":")[2];
                        finalSymbols.add(symbol);
                    });
                    symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                    // 取交易对的用户
                    symbols.stream().forEach(symbol -> {
                        String jsonString = new String();
                        Gson gson = new Gson();
                        if (cache.hasKey(BINANCE_QUOTATION_CACHE_10S + symbol + SPLIT + dataTime)) {
                            jsonString =
                                gson.toJson(cache.get(BINANCE_QUOTATION_CACHE_10S + symbol + SPLIT + dataTime));
                        } else {
                            jsonString = gson
                                .toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "10s"));
                        }
                        quotationExist(keys, symbol, jsonString);
                    });
                }
            } catch (Exception e) {
                log.info("停止推送");
                e.printStackTrace();
            }
        }
    }

    /**
     * 15S K线推送
     */
    @XxlJob("klineFifteenSeconds")
    public void klineFifteenSeconds() {
        if (DateUtil.isFifteenSecondsFlag(DateUtil.getDate13line())) {
            try {
                List<String> keys = cache.keys(KLINE_PREFIX_15S + "*");
                if (keys.size() > 0) {
                    Long dataTime = DateUtil.getReviseDate13line(null);
                    // 取交易对并去重
                    List<String> symbols = Lists.newArrayList();
                    List<String> finalSymbols = symbols;
                    keys.stream().forEach(key -> {
                        String symbol = key.split(":")[2];
                        finalSymbols.add(symbol);
                    });
                    symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                    // 取交易对的用户
                    symbols.stream().forEach(symbol -> {
                        String jsonString = new String();
                        Gson gson = new Gson();
                        if (cache.hasKey(BINANCE_QUOTATION_CACHE_15S + symbol + SPLIT + dataTime)) {
                            jsonString =
                                gson.toJson(cache.get(BINANCE_QUOTATION_CACHE_15S + symbol + SPLIT + dataTime));
                        } else {
                            jsonString = gson
                                .toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "15s"));
                        }
                        quotationExist(keys, symbol, jsonString);
                    });
                }
            } catch (Exception e) {
                log.info("停止推送");
                e.printStackTrace();
            }
        }
    }

    /**
     * 30S K线推送
     */
    @XxlJob("klineThirtySeconds")
    public void klineThirtySeconds() {
        if (DateUtil.isThirtySecondsFlag(DateUtil.getDate13line())) {
            try {
                List<String> keys = cache.keys(KLINE_PREFIX_30S + "*");
                if (keys.size() > 0) {
                    Long dataTime = DateUtil.getReviseDate13line(null);
                    // 取交易对并去重
                    List<String> symbols = Lists.newArrayList();
                    List<String> finalSymbols = symbols;
                    keys.stream().forEach(key -> {
                        String symbol = key.split(":")[2];
                        finalSymbols.add(symbol);
                    });
                    symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                    // 取交易对的用户
                    symbols.stream().forEach(symbol -> {
                        String jsonString = new String();
                        Gson gson = new Gson();
                        if (cache.hasKey(BINANCE_QUOTATION_CACHE_30S + symbol + SPLIT + dataTime)) {
                            jsonString =
                                gson.toJson(cache.get(BINANCE_QUOTATION_CACHE_30S + symbol + SPLIT + dataTime));
                        } else {
                            jsonString = gson
                                .toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "30s"));
                        }
                        quotationExist(keys, symbol, jsonString);
                    });
                }
            } catch (Exception e) {
                log.info("停止推送");
                e.printStackTrace();
            }
        }
    }

    /**
     * 1M K线推送
     */
    @XxlJob("klineOneMinute")
    public void klineOneMinute() {
        if (DateUtil.isOneMinuteFlag(DateUtil.getDate13line())) {
            try {
                List<String> keys = cache.keys(KLINE_PREFIX_1M + "*");
                if (keys.size() > 0) {
                    Long dataTime = DateUtil.getReviseDate13line(null);
                    // 取交易对并去重
                    List<String> symbols = Lists.newArrayList();
                    List<String> finalSymbols = symbols;
                    keys.stream().forEach(key -> {
                        String symbol = key.split(":")[2];
                        finalSymbols.add(symbol);
                    });
                    symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                    // 取交易对的用户
                    symbols.stream().forEach(symbol -> {
                        String jsonString = new String();
                        Gson gson = new Gson();
                        if (cache.hasKey(BINANCE_QUOTATION_CACHE_1M + symbol + SPLIT + dataTime)) {
                            jsonString = gson.toJson(cache.get(BINANCE_QUOTATION_CACHE_1M + symbol + SPLIT + dataTime));
                        } else {
                            jsonString = gson
                                .toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "1m"));
                        }
                        quotationExist(keys, symbol, jsonString);
                    });
                }
            } catch (Exception e) {
                log.info("停止推送");
                e.printStackTrace();
            }
        }
    }

    /**
     * 5M K线推送
     */
    @XxlJob("klineFiveMinute")
    public void klineFiveMinute() {
        try {
            List<String> keys = cache.keys(KLINE_PREFIX_5M + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[2];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    Gson gson = new Gson();
                    String jsonString =
                        gson.toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "5m"));
                    quotationExist(keys, symbol, jsonString);
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 15M K线推送
     */
    @XxlJob("klineFifteenMinute")
    public void klineFifteenMinute() {
        try {
            List<String> keys = cache.keys(KLINE_PREFIX_15M + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[2];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    Gson gson = new Gson();
                    String jsonString =
                        gson.toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "15m"));
                    quotationExist(keys, symbol, jsonString);
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 30M K线推送
     */
    @XxlJob("klineThirtyMinute")
    public void klineThirtyMinute() {
        try {
            List<String> keys = cache.keys(KLINE_PREFIX_30M + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[2];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    Gson gson = new Gson();
                    String jsonString =
                        gson.toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "30m"));
                    quotationExist(keys, symbol, jsonString);
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 1h K线推送
     */
    @XxlJob("klineOneHour")
    public void klineOneHour() {
        try {
            List<String> keys = cache.keys(KLINE_PREFIX_1H + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[2];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    Gson gson = new Gson();
                    String jsonString =
                        gson.toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "1h"));
                    quotationExist(keys, symbol, jsonString);
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 4h K线推送
     */
    @XxlJob("klineThreeHour")
    public void klineThreeHour() {
        try {
            List<String> keys = cache.keys(KLINE_PREFIX_4H + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[2];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    Gson gson = new Gson();
                    String jsonString =
                        gson.toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "4h"));
                    quotationExist(keys, symbol, jsonString);
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 1d K线推送
     */
    @XxlJob("klineOneDay")
    public void klineOneDay() {
        try {
            List<String> keys = cache.keys(KLINE_PREFIX_1D + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[2];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    Gson gson = new Gson();
                    String jsonString =
                        gson.toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "1d"));
                    quotationExist(keys, symbol, jsonString);
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 7d K线推送
     */
    @XxlJob("klineSevenDay")
    public void klineSevenDay() {
        try {
            List<String> keys = cache.keys(KLINE_PREFIX_7D + "*");
            if (keys.size() > 0) {
                Long dataTime = DateUtil.getReviseDate13line(null);
                // 取交易对并去重
                List<String> symbols = Lists.newArrayList();
                List<String> finalSymbols = symbols;
                keys.stream().forEach(key -> {
                    String symbol = key.split(":")[2];
                    finalSymbols.add(symbol);
                });
                symbols = finalSymbols.stream().distinct().collect(Collectors.toList());
                // 取交易对的用户
                symbols.stream().forEach(symbol -> {
                    Gson gson = new Gson();
                    String jsonString =
                        gson.toJson(quotationKLineProductMapper.selectKLine(symbol.toUpperCase(), dataTime, "7d"));
                    quotationExist(keys, symbol, jsonString);
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    private void quotationExist(List<String> keys, String symbol, String jsonString) {
        if (StringUtils.isBlank(jsonString) || "".equals(jsonString) || null == jsonString
            || "null".equals(jsonString)) {
            return;
        }
        keys.stream().filter(v -> v.contains(symbol)).forEach(key -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String userCode = key.split(":")[3];
                if (!WebSocketServer.queryUserIsOnline(userCode)) {
                    log.debug("userCode:{}", userCode);
                    log.debug("key:{}", key);
                    cache.remove(key);
                } else {
                    sendQuotationByUserCode(userCode, jsonString, WebsocketDateEnum.K_ACTUAL);
                }
            }, service);
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendQuotationByUserCode(String userCode, String jsonString, WebsocketDateEnum messageType) {
        if (null != jsonString && userCode != null) {
            StringBuffer buf = new StringBuffer("{\"dataType\":" + messageType.getColumnType() + ",\"data\":");
            buf.append(jsonString);
            Random random = new Random();
            SourceEn sourceEn = new SourceEn();
            Integer isOpen = sourceEn.isOpen[random.nextInt(sourceEn.isOpen.length)];
            if (messageType.getColumnType().intValue() == 0 && isOpen.intValue() > 9) {
                buf.deleteCharAt(buf.length() - 1);
                buf.append(",\"userName\":\"" + sourceEn.namesEn[random.nextInt(sourceEn.namesEn.length)]);
                int isReal = 0;
                if (random.nextInt(10) > 8) {
                    isReal = 1;
                    buf.append("\",\"orderAmount\":" + sourceEn.minPrice[random.nextInt(sourceEn.minPrice.length)]);
                } else {
                    buf.append("\",\"orderAmount\":" + sourceEn.maxPrice[random.nextInt(sourceEn.maxPrice.length)]);
                }
                buf.append(",\"tradeType\":" + random.nextInt(2));
                buf.append(",\"isReal\":" + isReal + "}");
            }
            buf.append("}");
            try {
                WebSocketServer.sendInfo(buf.toString(), userCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
