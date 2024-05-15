package com.ulla.service.impl;

import static com.ulla.constant.BinanceConstant.BINANCE_QUOTATION_CACHE_1S;
import static com.ulla.constant.BinanceSymbolConstant.SPLIT;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.enums.WebsocketDateEnum;
import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.SourceEn;
import com.ulla.common.utils.StringUtils;
import com.ulla.constant.BinanceConstant;
import com.ulla.constant.RedisConstant;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.mo.BalanceLogMo;
import com.ulla.modules.assets.service.BalanceLogService;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.binance.mapper.ProfitLossMapper;
import com.ulla.modules.binance.mapper.QuotationMapper;
import com.ulla.modules.binance.mo.ProfitLossMo;
import com.ulla.modules.binance.mo.QuotationKLineProductMo;
import com.ulla.modules.binance.mo.QuotationProductMo;
import com.ulla.modules.binance.vo.QuotationKLineCountVo;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.vo.OrderRankBySymbolVo;
import com.ulla.modules.business.vo.OrderRankVo;
import com.ulla.service.OrderAsyncTask;
import com.ulla.service.WebSocketServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 币安行情异步处理
 * @since 2023/2/14 16:51
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderAsyncTaskImpl implements OrderAsyncTask {

    final Cache cache;

    final QuotationMapper quotationMapper;

    final ProfitLossMapper profitLossMapper;

    final OrderMapper orderMapper;

    final BalanceService balanceService;

    final BalanceLogService balanceLogService;

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(50, 200, 10, TimeUnit.SECONDS,
        new LinkedBlockingDeque(1000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    /**
     * 算法干预
     * 
     * @param tradingEndTime
     * @param list
     */
    @Override
    @Async("order-async-executor")
    public void quotationInterpose(Long tradingEndTime, List<OrderMo> list) {
        try {

            Long todayTime = DateUtil.getTodayStartTime();
            AtomicReference<BigDecimal> todayProfit = new AtomicReference<>();
            AtomicReference<BigDecimal> todayLoss = new AtomicReference<>();
            todayProfit.getAndSet(new BigDecimal("0.0"));
            todayLoss.getAndSet(new BigDecimal("0.0"));
            ProfitLossMo profitLossMo = profitLossMapper.selectById(todayTime);
            if (null != profitLossMo) {
                todayProfit.getAndSet(new BigDecimal(profitLossMo.getProfit()));
                todayLoss.getAndSet(new BigDecimal(profitLossMo.getLoss()));
            }
            AtomicReference<BigDecimal> tempTodayProfit = new AtomicReference<>();
            AtomicReference<BigDecimal> tempTodayLoss = new AtomicReference<>();
            tempTodayProfit.getAndSet(todayProfit.get());
            tempTodayLoss.getAndSet(todayLoss.get());
            Map<String, List<OrderMo>> map = list.stream().collect(Collectors.groupingBy(OrderMo::getPairs));
            List<OrderMo> fallList = Lists.newArrayList();
            Long streamTime = tradingEndTime - 999;
            for (String symbol : map.keySet()) {
                List<OrderMo> tempListOrder = map.get(symbol);

                // 结算点行情
                QuotationProductMo quotationProductMo = new QuotationProductMo();
                if (cache.hasKey(BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + SPLIT + streamTime)) {
                    Gson gson = new Gson();
                    quotationProductMo = gson.fromJson(
                        (cache.get(BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + SPLIT + streamTime)).toString(),
                        QuotationProductMo.class);
                } else {
                    quotationProductMo = quotationMapper.selectQuotationByStreamTime(symbol.toLowerCase(), streamTime);
                }
                QuotationProductMo finalQuotationProductMo = quotationProductMo;
                Map<Integer, Map<Integer, List<OrderMo>>> mapMap = tempListOrder.stream()
                    .collect(Collectors.groupingBy(OrderMo::getOpenClose, Collectors.groupingBy(orderMo -> {
                        if (orderMo.getOpenClose().intValue() == 1) {
                            if (new BigDecimal(orderMo.getPrice())
                                .compareTo(new BigDecimal(finalQuotationProductMo.getClosePrice())) < 0) {
                                todayLoss.getAndSet(orderMo
                                    .getOrderAmount().multiply(new BigDecimal(orderMo.getProfitPercent())
                                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN))
                                    .add(todayLoss.get()));
                                return 1;
                            } else {
                                todayProfit.getAndSet(todayProfit.get().add(orderMo.getOrderAmount()));
                                return 0;
                            }
                        } else {
                            if (new BigDecimal(orderMo.getPrice())
                                .compareTo(new BigDecimal(finalQuotationProductMo.getClosePrice())) > 0) {
                                todayLoss.getAndSet(orderMo
                                    .getOrderAmount().multiply(new BigDecimal(orderMo.getProfitPercent())
                                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN))
                                    .add(todayLoss.get()));
                                return 1;
                            } else {
                                todayProfit.getAndSet(todayProfit.get().add(orderMo.getOrderAmount()));
                                return 0;
                            }
                        }
                    })));
                if (null != mapMap.get(1) && null != mapMap.get(1).get(1) && mapMap.get(1).get(1).size() > 0) {
                    fallList.addAll(mapMap.get(1).get(1));
                }
                if (null != mapMap.get(0) && null != mapMap.get(0).get(1) && mapMap.get(0).get(1).size() > 0) {
                    fallList.addAll(mapMap.get(0).get(1));
                }
            }

            // 算法干预
            if (fallList.size() > 0 && todayProfit.get().compareTo(todayLoss.get()) <= 0) {
                algorithm(todayProfit, todayLoss, tempTodayProfit, tempTodayLoss, list, fallList, streamTime);
            }

            if (null == profitLossMo) {
                profitLossMo = new ProfitLossMo();
                profitLossMo.setId(todayTime);
                profitLossMo.setProfit(todayProfit.get().toString());
                profitLossMo.setLoss(todayLoss.get().toString());
                profitLossMapper.insert(profitLossMo);
            } else {
                profitLossMo.setProfit(todayProfit.get().toString());
                profitLossMo.setLoss(todayLoss.get().toString());
                profitLossMapper.updateById(profitLossMo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 止亏算法干预
     * 
     * @param list
     */
    public void algorithm(AtomicReference<BigDecimal> todayProfit, AtomicReference<BigDecimal> todayLoss,
        AtomicReference<BigDecimal> tempTodayProfit, AtomicReference<BigDecimal> tempTodayLoss, List<OrderMo> list,
        List<OrderMo> fallList, Long streamTime) {
        BigDecimal fallPrice = new BigDecimal("0.0");
        log.info("----------------------止亏算法干预----------------------");
        fallList = fallList.stream().sorted(Comparator.comparing(OrderMo::getOrderAmount).reversed())
            .collect(Collectors.toList());
        int index = 0;

        Map<String, QuotationProductMo> mapQuotation = new HashMap<>();

        do {
            AtomicReference<BigDecimal> algorithmTodayProfit = new AtomicReference<>();
            AtomicReference<BigDecimal> algorithmTodayLoss = new AtomicReference<>();
            algorithmTodayProfit.getAndSet(tempTodayProfit.get());
            algorithmTodayLoss.getAndSet(tempTodayLoss.get());

            OrderMo tempOrderMo = fallList.get(index++);

            QuotationProductMo quotation = new QuotationProductMo();
            if (cache.hasKey(BINANCE_QUOTATION_CACHE_1S + tempOrderMo.getPairs().toUpperCase() + SPLIT + streamTime)) {
                Gson gson = new Gson();
                quotation = gson.fromJson(
                    (cache.get(BINANCE_QUOTATION_CACHE_1S + tempOrderMo.getPairs().toUpperCase() + SPLIT + streamTime))
                        .toString(),
                    QuotationProductMo.class);
            } else {
                quotation =
                    quotationMapper.selectQuotationByStreamTime(tempOrderMo.getPairs().toLowerCase(), streamTime);
            }
            quotation.setClosePrice(tempOrderMo.getPrice());
            mapQuotation.put(tempOrderMo.getPairs(), quotation);

            Map<String, List<OrderMo>> map = list.stream().collect(Collectors.groupingBy(OrderMo::getPairs));

            for (String symbol : map.keySet()) {
                List<OrderMo> tempListOrder = map.get(symbol);
                AtomicReference<QuotationProductMo> quotationProductMo = new AtomicReference<>();
                if (symbol.equalsIgnoreCase(tempOrderMo.getPairs())) {
                    quotationProductMo.getAndSet(quotation);
                } else {
                    if (cache.hasKey(BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + SPLIT + streamTime)) {
                        Gson gson = new Gson();
                        quotationProductMo.getAndSet(gson.fromJson(
                            (cache.get(BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + SPLIT + streamTime))
                                .toString(),
                            QuotationProductMo.class));
                    } else {
                        quotationProductMo
                            .getAndSet(quotationMapper.selectQuotationByStreamTime(symbol.toLowerCase(), streamTime));
                    }
                }

                Map<Integer, Map<Integer, List<OrderMo>>> mapMap = tempListOrder.stream()
                    .collect(Collectors.groupingBy(OrderMo::getOpenClose, Collectors.groupingBy(orderMo -> {
                        if (orderMo.getOpenClose().intValue() == 1) {
                            if (new BigDecimal(orderMo.getPrice())
                                .compareTo(new BigDecimal(quotationProductMo.get().getClosePrice())) < 0) {
                                algorithmTodayLoss.getAndSet(orderMo
                                    .getOrderAmount().multiply(new BigDecimal(orderMo.getProfitPercent())
                                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN))
                                    .add(algorithmTodayLoss.get()));
                                return 1;
                            } else {
                                algorithmTodayProfit
                                    .getAndSet(algorithmTodayProfit.get().add(orderMo.getOrderAmount()));
                                return 0;
                            }
                        } else {
                            if (new BigDecimal(orderMo.getPrice())
                                .compareTo(new BigDecimal(quotationProductMo.get().getClosePrice())) > 0) {
                                algorithmTodayLoss.getAndSet(orderMo
                                    .getOrderAmount().multiply(new BigDecimal(orderMo.getProfitPercent())
                                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_DOWN))
                                    .add(algorithmTodayLoss.get()));
                                return 1;
                            } else {
                                algorithmTodayProfit
                                    .getAndSet(algorithmTodayProfit.get().add(orderMo.getOrderAmount()));
                                return 0;
                            }
                        }
                    })));
            }

            fallPrice = algorithmTodayLoss.get().subtract(algorithmTodayProfit.get());
            todayProfit.getAndSet(algorithmTodayProfit.get());
            todayLoss.getAndSet(algorithmTodayLoss.get());
        } while (fallPrice.compareTo(new BigDecimal("0.0")) > 0);

        mapQuotation.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()).forEach(item -> {
            item.getOrderRangeId();
            Long startDataTime = streamTime - 15001;
            Long endDataTime = streamTime + 5001;
            List<QuotationProductMo> algorithmQuotation =
                quotationMapper.selectByTradingTime(item.getSymbol().toLowerCase(), startDataTime, endDataTime);
            BigDecimal startPrice = new BigDecimal(algorithmQuotation.get(0).getClosePrice());
            BigDecimal amongPrice = new BigDecimal(item.getClosePrice());
            BigDecimal endPrice = new BigDecimal(algorithmQuotation.get(algorithmQuotation.size() - 1).getClosePrice());
            BigDecimal spacingPrice = new BigDecimal("0.0");
            BigDecimal returnPrice = new BigDecimal("0.0");
            long marking =
                algorithmQuotation.stream().filter(e -> e.getTradingRangeId().equals(item.getTradingRangeId())).count()
                    - 1;
            if (startPrice.compareTo(amongPrice) > 0) {
                spacingPrice = startPrice.subtract(amongPrice);
            } else if (startPrice.compareTo(amongPrice) < 0) {
                spacingPrice = amongPrice.subtract(startPrice);
            }
            if (endPrice.compareTo(amongPrice) > 0) {
                returnPrice = endPrice.subtract(amongPrice);
            } else if (endPrice.compareTo(amongPrice) < 0) {
                returnPrice = amongPrice.subtract(endPrice);
            }
            Random rand = new Random();
            for (int i = 0; i < marking; i++) {
                QuotationProductMo mo = algorithmQuotation.get(0);
                String symbol = mo.getSymbol();
                Long delayTime = mo.getStreamTime();
                if (i < marking) {
                    double midLongitude = spacingPrice.doubleValue();
                    float newLongitude = rand.nextFloat();
                    BigDecimal sa1 = new BigDecimal(newLongitude);
                    BigDecimal sa2 = new BigDecimal(midLongitude);
                    BigDecimal sa3 = sa1.multiply(sa2);
                    BigDecimal sa4 = sa3.add(startPrice);
                    mo.setClosePrice(sa4.setScale(10, BigDecimal.ROUND_DOWN).toString());
                    quotationMapper.updateById(mo);
                    // 更新缓存1S数据
                    cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol + BinanceConstant.SPLIT + delayTime,
                        mo, 50000l, TimeUnit.MILLISECONDS);
                } else if (i > marking && i != algorithmQuotation.size() - 2) {
                    double midLongitude = returnPrice.doubleValue();
                    float newLongitude = rand.nextFloat();
                    BigDecimal sa1 = new BigDecimal(newLongitude);
                    BigDecimal sa2 = new BigDecimal(midLongitude);
                    BigDecimal sa3 = sa1.multiply(sa2);
                    BigDecimal sa4 = sa3.add(endPrice);
                    mo.setClosePrice(sa4.setScale(10, BigDecimal.ROUND_DOWN).toString());
                    quotationMapper.updateById(mo);
                    // 更新缓存1S数据
                    cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol + BinanceConstant.SPLIT + delayTime,
                        mo, 50000l, TimeUnit.MILLISECONDS);
                } else if (i == marking) {
                    quotationMapper.updateById(mo);
                    // 更新缓存1S数据
                    cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol + BinanceConstant.SPLIT + delayTime,
                        mo, 50000l, TimeUnit.MILLISECONDS);
                }
                try {
                    CompletableFuture<Void> future5s = CompletableFuture.runAsync(() -> {
                        log.info("future5s介入线程开始.....当前线程---->{}", Thread.currentThread().getName());
                        // 5S 数据处理
                        extractedFiveSeconds(mo);
                        log.info("future5s介入线程结束.....");
                    }, executor);
                    CompletableFuture<Void> future10s = CompletableFuture.runAsync(() -> {
                        log.info("future10s介入线程开始.....当前线程---->{}", Thread.currentThread().getName());
                        // 10S 数据处理
                        extractedTenSeconds(mo);
                        log.info("future10s介入线程结束.....");
                    }, executor);
                    CompletableFuture<Void> future15s = CompletableFuture.runAsync(() -> {
                        log.info("future15s介入线程开始.....当前线程---->{}", Thread.currentThread().getName());
                        // 15S 数据处理
                        extractedFifteenSeconds(mo);
                        log.info("future15s介入线程结束.....");
                    }, executor);
                    CompletableFuture<Void> future30s = CompletableFuture.runAsync(() -> {
                        log.info("future30s介入线程开始.....当前线程---->{}", Thread.currentThread().getName());
                        // 30S 数据处理
                        extractedThirtySeconds(mo);
                        log.info("future30s介入线程结束.....");
                    }, executor);
                    CompletableFuture<Void> future1m = CompletableFuture.runAsync(() -> {
                        log.info("future1m介入线程开始.....当前线程---->{}", Thread.currentThread().getName());
                        // 1M 数据处理
                        extractedOneMinute(mo);
                        log.info("future1m介入线程结束.....");
                    }, executor);

                    CompletableFuture<Void> allOf =
                        CompletableFuture.allOf(future5s, future10s, future15s, future30s, future1m);

                    allOf.get(); // 阻塞在这个位置，等待所有线程的完成
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        log.info("----------------------止亏算法结束----------------------");
    }

    /**
     * 订单结算
     * 
     * @param orderMo
     */
    @Override
    @Async("order-async-executor")
    public void orderCount(OrderMo orderMo) {
        Long streamTime = orderMo.getEndTime() - 999;
        if (null == orderMo.getPairs()) {
            return;
        }
        QuotationProductMo quotationProductMo = new QuotationProductMo();
        if (cache.hasKey(BINANCE_QUOTATION_CACHE_1S + orderMo.getPairs().toUpperCase() + SPLIT + streamTime)) {
            Gson gson = new Gson();
            quotationProductMo = gson.fromJson(
                (cache.get(BINANCE_QUOTATION_CACHE_1S + orderMo.getPairs().toUpperCase() + SPLIT + streamTime))
                    .toString(),
                QuotationProductMo.class);
        } else {
            quotationProductMo =
                quotationMapper.selectQuotationByStreamTime(orderMo.getPairs().toLowerCase(), streamTime);
        }
        if (null == quotationProductMo) {
            // TODO 无行情结算处理，暂时不处理
            return;
        }
        BigDecimal closePrice = new BigDecimal(quotationProductMo.getClosePrice());
        orderMo.setEndPrice(quotationProductMo.getClosePrice());
        // 判断涨跌
        if (orderMo.getOpenClose().intValue() == 1) {
            if (new BigDecimal(orderMo.getPrice()).compareTo(closePrice) < 0) {
                orderMo.setIfProfit(1);
            } else {
                orderMo.setIfProfit(0);
                orderMo.setBenefit(new BigDecimal("0.00"));
            }
        } else {
            if (new BigDecimal(orderMo.getPrice()).compareTo(closePrice) > 0) {
                orderMo.setIfProfit(1);
            } else {
                orderMo.setIfProfit(0);
                orderMo.setBenefit(new BigDecimal("0.00"));
            }
        }
        // 盈利计算
        if (orderMo.getIfProfit().intValue() == 1) {
            BigDecimal orderAmount = orderMo.getOrderAmount();
            BigDecimal profitPercent =
                new BigDecimal(orderMo.getProfitPercent()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_DOWN);
            BigDecimal benefit = orderAmount.multiply(profitPercent).add(orderAmount);
            BalanceChangeVo balanceChangeVo = new BalanceChangeVo();
            balanceChangeVo.setUid(orderMo.getUid());
            // 金额计算
            if (orderMo.getType().intValue() == 0) {
                balanceChangeVo.setVirtualAmount(benefit);
            } else {
                QueryWrapper<BalanceLogMo> wrapper = new QueryWrapper();
                wrapper.eq("business_no", orderMo.getOrderCode());
                List<BalanceLogMo> listLog = balanceLogService.list(wrapper);
                AtomicReference<BigDecimal> realAmount = new AtomicReference<>(new BigDecimal("0.0"));
                listLog.stream().forEach(item -> {
                    if (item.getType().intValue() == 1) {
                        realAmount.set(item.getAmount());
                    }
                });

                BigDecimal benefitPercent = new BigDecimal("1.0");
                if (realAmount.get().compareTo(new BigDecimal("0.0")) > 0) {
                    benefitPercent = realAmount.get().divide(orderAmount, 2, BigDecimal.ROUND_DOWN);
                    balanceChangeVo.setAmount(benefit.multiply(benefitPercent));
                    benefitPercent = new BigDecimal("1.0").subtract(benefitPercent);
                }
                balanceChangeVo.setBonusAmount(benefit.multiply(benefitPercent));
            }
            balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.EXCHANGE);
            balanceChangeVo.setBusinessNo(orderMo.getOrderCode());
            balanceService.transactionChangeBalanceAndSaveLog(balanceChangeVo);
            orderMo.setBenefit(benefit);
            orderMo.setUpdateBy(0l);
            orderMapper.updateById(orderMo);
        } else {
            orderMapper.updateById(orderMo);
        }
    }

    @Override
    @Async("order-async-executor")
    public void orderRank(String userCode, String symbol) {
        Long minuteEndTime = DateUtil.getMinuteEndTime(null);
        StringBuffer buf = new StringBuffer("{\"dataType\":");
        Gson gson = new Gson();
        StringBuffer finalBuf = new StringBuffer();
        Random random = new Random();
        if (StringUtils.isNotBlank(symbol)) {
            // TODO 这里的真实数据过少，目前采用随机数
            /* Long beforeStartTime = minuteEndTime - 59999;
            OrderRankBySymbolVo orderRankBySymbolVo =
                orderMapper.orderRankBySymbol(symbol, beforeStartTime, minuteEndTime);*/
            OrderRankBySymbolVo orderRankBySymbolVo = new OrderRankBySymbolVo();
            orderRankBySymbolVo.setPairs(symbol);

            Integer userCount = Integer.valueOf(cache.get(RedisConstant.User.VIRTUALLY_USER_ONLINE_NUM).toString());
            Integer orderCount = Integer.valueOf(cache.get(RedisConstant.Order.VIRTUALLY_ORDER_COUNT).toString());
            Integer orderTotalAmount =
                Integer.valueOf(cache.get(RedisConstant.Order.VIRTUALLY_ORDER_TOTAL_AMOUNT).toString());
            orderRankBySymbolVo.setUserCount(userCount / (random.nextInt(20) + 10) + random.nextInt(20));
            orderRankBySymbolVo.setOrderCount(orderCount / (random.nextInt(20) + 10) + random.nextInt(20));
            orderRankBySymbolVo.setOrderTotalAmount(orderTotalAmount / (random.nextInt(20) + 10) + random.nextInt(20));
            Integer percent = random.nextInt(100);
            orderRankBySymbolVo.setRisePercent(percent);
            orderRankBySymbolVo.setDropPercent(100 - percent);

            orderRankBySymbolVo.setTodayUserCount(userCount);
            orderRankBySymbolVo.setTodayOrderCount(orderCount);
            orderRankBySymbolVo.setTodayOrderTotalAmount(orderTotalAmount);
            buf.append(WebsocketDateEnum.ORDER_RANK_BY_SYMBOL.getColumnType());
            finalBuf.append(gson.toJson(orderRankBySymbolVo));
        } else {
            Long beforeStartTime = minuteEndTime - 119999;
            OrderRankVo orderRankVo = new OrderRankVo();
            orderRankVo.setRankTime(DateUtil.toString(beforeStartTime, "HH:mm:ss"));
            orderRankVo.setRankTimeStamp(beforeStartTime);
            orderRankVo.setOrderTotalAmount(600000 + random.nextInt(200000));
            orderRankVo.setOrderCount(3000 + random.nextInt(600));
            orderRankVo.setUserRank(getUserRank());
            buf.append(WebsocketDateEnum.ORDER_RANK.getColumnType());
            finalBuf.append(gson.toJson(orderRankVo));
        }
        buf.append(",\"data\":");
        try {
            if (finalBuf.length() != 0) {
                buf.append(finalBuf);
            }
            buf.append("}");
            WebSocketServer.sendInfo(buf.toString(), userCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<OrderRankVo.UserRank> getUserRank() {
        List<OrderRankVo.UserRank> list = Lists.newArrayList();
        Random random = new Random();
        SourceEn sourceEn = new SourceEn();
        List<String> listNames = Arrays.asList(sourceEn.namesEn);
        OrderRankVo.UserRank userRank = new OrderRankVo.UserRank();
        String name = listNames.get(random.nextInt(listNames.size()));
        list = list.stream().filter(f -> !f.equals(name)).collect(Collectors.toList());
        userRank.setName(name);
        Gson gson = new Gson();
        Type type = new TypeToken<List<TransactionCategoryChildMo>>() {}.getType();
        List<TransactionCategoryChildMo> listSymbol =
            gson.fromJson(cache.get("binance:api:symbolList").toString(), type);
        listSymbol = listSymbol.stream().filter(f -> f.getIsPopular().intValue() == 1).collect(Collectors.toList());

        userRank.setPairs(listSymbol.get(random.nextInt(listSymbol.size())).getChildName());
        userRank.setBenefit(random.nextInt(500) + 1500);
        list.add(userRank);
        userRank = new OrderRankVo.UserRank();
        String name2 = listNames.get(random.nextInt(listNames.size()));
        list = list.stream().filter(f -> !f.equals(name2)).collect(Collectors.toList());
        userRank.setName(name2);
        userRank.setPairs(listSymbol.get(random.nextInt(listSymbol.size())).getChildName());
        userRank.setBenefit(random.nextInt(1500) + 500);
        list.add(userRank);
        userRank = new OrderRankVo.UserRank();
        String name3 = listNames.get(random.nextInt(listNames.size()));
        list = list.stream().filter(f -> !f.equals(name3)).collect(Collectors.toList());
        userRank.setName(name3);
        userRank.setPairs(listSymbol.get(random.nextInt(listSymbol.size())).getChildName());
        userRank.setBenefit(random.nextInt(1500) + 500);
        list.add(userRank);
        userRank = new OrderRankVo.UserRank();
        String name4 = listNames.get(random.nextInt(listNames.size()));
        list = list.stream().filter(f -> !f.equals(name4)).collect(Collectors.toList());
        userRank.setName(name4);
        userRank.setPairs(listSymbol.get(random.nextInt(listSymbol.size())).getChildName());
        userRank.setBenefit(random.nextInt(1500) + 500);
        list.add(userRank);
        userRank = new OrderRankVo.UserRank();
        String name5 = listNames.get(random.nextInt(listNames.size()));
        list = list.stream().filter(f -> !f.equals(name5)).collect(Collectors.toList());
        userRank.setName(name5);
        userRank.setPairs(listSymbol.get(random.nextInt(listSymbol.size())).getChildName());
        userRank.setBenefit(random.nextInt(1500) + 500);
        list.add(userRank);
        return list.stream().sorted(Comparator.comparing(OrderRankVo.UserRank::getBenefit).reversed())
            .collect(Collectors.toList());
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
        if (!(mo.getStreamTime().compareTo(endTime) == 0)) {
            mo.setCloseFlag(0);
        }
        // 数据计算
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(BinanceConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                vo = tempCount(mo, endTime);
                cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol(), vo, 8000l, TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, BinanceConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol());
            }
        } else {
            QuotationKLineCountVo vo = tempCount(mo, endTime);
            cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_5S + mo.getSymbol(), vo, 8000l, TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isFiveSecondsFlag(time)) {
            // 数据处理逻辑
            if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol())) {
                List<QuotationKLineProductMo> listQuotation = (List<QuotationKLineProductMo>)cache
                    .get(BinanceConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol());
                listQuotation.add(mo);
                if (listQuotation.size() > 5) {
                    binanceKLine5SQuotation(listQuotation);
                    cache.remove(BinanceConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol());
                } else {
                    cache.put(BinanceConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol(), listQuotation, 50000l,
                        TimeUnit.MILLISECONDS);
                }
            } else {
                List<QuotationKLineProductMo> listQuotation = Lists.newArrayList();
                listQuotation.add(mo);
                cache.put(BinanceConstant.BINANCE_QUOTATION_DATA_5S + mo.getSymbol(), listQuotation, 50000l,
                    TimeUnit.MILLISECONDS);
            }
            // 缓存5S数据
            cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_5S + mo.getSymbol() + BinanceConstant.SPLIT + time, mo,
                50000l, TimeUnit.MILLISECONDS);
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
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(BinanceConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol(), tempCount(mo, endTime), 13000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, BinanceConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol());
            }
        } else {
            cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_10S + mo.getSymbol(), tempCount(mo, endTime), 13000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isTenSecondsFlag(time)) {
            // 缓存10S数据
            cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_10S + mo.getSymbol() + BinanceConstant.SPLIT + time, mo,
                40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceKLineByInterval(mo, "10s");
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
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(BinanceConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol(), tempCount(mo, endTime), 18000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, BinanceConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol());
            }
        } else {
            cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_15S + mo.getSymbol(), tempCount(mo, endTime), 18000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isFifteenSecondsFlag(time)) {
            // 缓存15S数据
            cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_15S + mo.getSymbol() + BinanceConstant.SPLIT + time, mo,
                40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceKLineByInterval(mo, "15s");
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
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(BinanceConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol(), tempCount(mo, endTime), 34000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, BinanceConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol());
            }
        } else {
            cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_30S + mo.getSymbol(), tempCount(mo, endTime), 34000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isThirtySecondsFlag(time)) {
            // 缓存30S数据
            cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_30S + mo.getSymbol() + BinanceConstant.SPLIT + time, mo,
                40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceKLineByInterval(mo, "30s");
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
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol())) {
            QuotationKLineCountVo vo =
                (QuotationKLineCountVo)cache.get(BinanceConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol());
            if (mo.getStreamTime() > vo.getEndTime()) {
                mo.setOpenPrice(vo.getClosePrice());
                cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol(), tempCount(mo, endTime), 64000l,
                    TimeUnit.MILLISECONDS);
            } else {
                keepRecords(mo, vo, BinanceConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol());
            }
        } else {
            cache.put(BinanceConstant.BINANCE_QUOTATION_TEMP_1M + mo.getSymbol(), tempCount(mo, endTime), 64000l,
                TimeUnit.MILLISECONDS);
        }

        if (DateUtil.isOneMinuteFlag(time)) {
            // 缓存1M数据
            cache.put(BinanceConstant.BINANCE_QUOTATION_CACHE_1M + mo.getSymbol() + BinanceConstant.SPLIT + time, mo,
                40000l, TimeUnit.MILLISECONDS);
            if (mo.getCloseFlag().intValue() == 1) {
                // 数据处理逻辑
                binanceKLineByInterval(mo, "1m");
            }
        }
    }

    public static <K> Predicate<K> distinctPredicate(Function<K, Object> function) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>();
        return (t) -> null == map.putIfAbsent(function.apply(t), true);
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

        mo.setHighPrice(vo.getHighPrice());
        mo.setLowPrice(vo.getLowPrice());
        mo.setTurnoverNum(vo.getTurnoverNum());
        mo.setTurnoverAmount(vo.getTurnoverAmount());
        mo.setTranLimit(vo.getTranLimit());
    }

    @Async("order-async-executor")
    public void binanceKLine5SQuotation(List<QuotationKLineProductMo> listQuotation) {
        try {
            List<QuotationKLineProductMo> newList =
                listQuotation.stream().filter(distinctPredicate(m -> m.getStreamTime())).collect(Collectors.toList());
            newList.stream().forEach(item -> {
                quotationMapper.insertFiveSeconds(item);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async("order-async-executor")
    public void binanceKLineByInterval(QuotationKLineProductMo mo, String intervalType) {
        try {
            quotationMapper.binanceKLineByInterval(mo, intervalType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
