package com.ulla.service.impl;

import static com.ulla.constant.BinanceSymbolConstant.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.enums.WebsocketDateEnum;
import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.TokenUtils;
import com.ulla.constant.BinanceConstant;
import com.ulla.constant.RedisConstant;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.binance.mapper.QuotationKLineProductMapper;
import com.ulla.modules.binance.mapper.QuotationMapper;
import com.ulla.modules.binance.mo.QuotationKLineProductMo;
import com.ulla.modules.binance.mo.QuotationProductMo;
import com.ulla.modules.binance.qo.BinanceQuotationQo;
import com.ulla.modules.binance.qo.OrderRankQo;
import com.ulla.modules.binance.qo.SettleQo;
import com.ulla.modules.binance.vo.QuotationProductVo;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mapper.TransactionConfigMapper;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.mo.TransactionCategoryMo;
import com.ulla.modules.business.mo.TransactionConfigMo;
import com.ulla.modules.business.qo.TransactionCategoryChildQo;
import com.ulla.modules.business.qo.TransactionCategoryQo;
import com.ulla.modules.business.qo.WebsocketTransactionCategoryQo;
import com.ulla.modules.business.service.TransactionCategoryChildService;
import com.ulla.modules.business.service.TransactionCategoryService;
import com.ulla.modules.business.vo.OrderCalculationVo;
import com.ulla.modules.business.vo.OrderResultVo;
import com.ulla.modules.business.vo.TransactionCategoryChildVo;
import com.ulla.modules.business.vo.UserOrderCalculationVo;
import com.ulla.service.OrderAsyncTask;
import com.ulla.service.WebSocketQuotationService;
import com.ulla.service.WebSocketServer;
import com.ulla.task.InterposeXxlJobServer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSocketQuotationServiceImpl implements WebSocketQuotationService {

    final Cache cache;

    final UserMapper userMapper;

    final QuotationMapper quotationMapper;

    final OrderMapper orderMapper;

    final TransactionCategoryService transactionCategoryService;

    final TransactionCategoryChildService transactionCategoryChildService;

    final QuotationKLineProductMapper quotationKLineProductMapper;

    final TransactionConfigMapper transactionConfigMapper;

    final OrderAsyncTask orderAsyncTask;

    final InterposeXxlJobServer interposeXxlJobServer;

    /**
     * 根据symbol查看行情
     * 
     * @param userCode
     * @param binanceQuotationQo
     */
    @Override
    public void openQuotation(String userCode, BinanceQuotationQo binanceQuotationQo) {
        List<Object> keys = cache.keys(QUOTATION_PREFIX + "*:" + userCode);
        log.debug(keys.toString());
        cache.multiDel(keys);
        Long endDataTime = DateUtil.getReviseDate13line(null);
        Long startDataTime = endDataTime - 201000;
        List<QuotationProductMo> listHis =
            quotationMapper.selectHisList(binanceQuotationQo.getSymbol().toLowerCase(), startDataTime, endDataTime);
        StringBuffer buf = new StringBuffer("{\"dataType\":");
        buf.append(WebsocketDateEnum.INITIAL.getColumnType());
        buf.append(",\"requestUuid\":\"" + binanceQuotationQo.getRequestUuid());
        buf.append("\",\"data\":[");

        StringBuffer finalBuf = new StringBuffer();
        listHis.stream().forEach(item -> {
            finalBuf.append(JSONObject.toJSONString(item) + ",");
        });
        try {
            if (finalBuf.length() != 0) {
                String s = finalBuf.substring(0, finalBuf.length() - 1) + "]}";
                buf.append(s);
            } else {
                buf.append("]}");
            }
            WebSocketServer.sendInfo(buf.toString(), userCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache.put(QUOTATION_PREFIX + binanceQuotationQo.getSymbol() + SPLIT + userCode, userCode);
    }

    /**
     * 用户退出关闭数据推送
     * 
     * @param userCode
     */
    @Override
    public void closeQuotation(String userCode) {
        List<Object> keys = cache.keys("*" + userCode);
        cache.multiDel(keys);
    }

    /**
     * 根据时间查询历史行情
     * 
     * @param userCode
     * @param binanceQuotationQo
     */
    @Override
    public void queryHisQuotation(String userCode, BinanceQuotationQo binanceQuotationQo) {
        Long startDataTime;
        Long endDataTime;
        if (binanceQuotationQo.getStartDataTime() != null && binanceQuotationQo.getEndDataTime() != null) {
            startDataTime = binanceQuotationQo.getStartDataTime();
            endDataTime = binanceQuotationQo.getEndDataTime();
        } else if (binanceQuotationQo.getStartDataTime() == null) {
            endDataTime = binanceQuotationQo.getEndDataTime();
            startDataTime = endDataTime - 201000;
        } else {
            startDataTime = binanceQuotationQo.getStartDataTime();
            endDataTime = startDataTime + 201000;
        }
        Long beforeTime = DateUtil.getReviseDate13line(null);
        if (endDataTime.longValue() > beforeTime.longValue()) {
            endDataTime = beforeTime - 1000;
        }
        List<QuotationProductMo> listHis =
            quotationMapper.selectHisList(binanceQuotationQo.getSymbol().toLowerCase(), startDataTime, endDataTime);;
        StringBuffer buf = new StringBuffer("{\"dataType\":");
        buf.append(WebsocketDateEnum.HIS.getColumnType());
        buf.append(",\"requestUuid\":\"" + binanceQuotationQo.getRequestUuid());
        buf.append("\",\"data\":[");
        StringBuffer finalBuf = new StringBuffer();
        listHis.stream().forEach(item -> {
            finalBuf.append(JSONObject.toJSONString(item) + ",");
        });
        try {
            if (finalBuf.length() != 0) {
                String s = finalBuf.substring(0, finalBuf.length() - 1) + "]}";
                buf.append(s);
            } else {
                buf.append("]}");
            }
            WebSocketServer.sendInfo(buf.toString(), userCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getSymbolList(String userCode, WebsocketTransactionCategoryQo websocketTransactionCategoryQo) {
        Integer categoryType = websocketTransactionCategoryQo.getCategoryType();
        StringBuffer buf = new StringBuffer();
        StringBuffer finalBuf = new StringBuffer();
        if (categoryType != null && categoryType == 1) {
            buf.append("{\"dataType\":");
            buf.append(WebsocketDateEnum.TRANSACTION_CATEGORY.getColumnType());
            buf.append(",\"requestUuid\":\"" + websocketTransactionCategoryQo.getRequestUuid());
            buf.append("\",\"data\":[");
            TransactionCategoryQo qo = new TransactionCategoryQo();
            qo.setSortRule(1);
            qo.setUsingFlag(0);
            List<TransactionCategoryMo> list = transactionCategoryService.getList(qo);
            list.stream().forEach(item -> {
                finalBuf.append(JSONObject.toJSONString(item) + ",");
            });
        } else if (categoryType != null && categoryType == 2) {
            log.info("获取缓存交易对列表：{}-{}", websocketTransactionCategoryQo.getCategoryId(),
                websocketTransactionCategoryQo.getRequestUuid());

            buf.append("{\"dataType\":");
            buf.append(WebsocketDateEnum.TRANSACTION_CATEGORY_CHILD.getColumnType());
            buf.append(",\"requestUuid\":\"" + websocketTransactionCategoryQo.getRequestUuid());
            buf.append("\",\"data\":[");
            TransactionCategoryChildQo transactionCategoryChildQo = new TransactionCategoryChildQo();
            BeanUtils.copyProperties(websocketTransactionCategoryQo, transactionCategoryChildQo);
            transactionCategoryChildQo.setSortRule(0);
            transactionCategoryChildQo.setUsingFlag(0);
            Gson gson = new Gson();
            Type type = new TypeToken<List<TransactionCategoryChildMo>>() {}.getType();
            List<TransactionCategoryChildMo> listChild =
                gson.fromJson(cache.get("binance:api:symbolList").toString(), type);
            log.info("获取缓存交易对列表({})：{}", listChild.size(), listChild);
            Long categoryId = websocketTransactionCategoryQo.getCategoryId();
            if (ObjectUtils.isNotEmpty(categoryId)) {
                listChild =
                    listChild.stream().filter(k -> k.getCategoryId().equals(categoryId)).collect(Collectors.toList());
            }
            log.info("过滤后的交易对列表({})：{}", listChild.size(), listChild);
            listChild.stream().forEach(item -> {
                TransactionCategoryChildVo temp = new TransactionCategoryChildVo();
                BeanUtils.copyProperties(item, temp);
                putTrend(temp);
                finalBuf.append(JSONObject.toJSONString(temp) + ",");
            });
        } else {
            try {
                String message = "{\"requestUuid\":\"" + websocketTransactionCategoryQo.getRequestUuid()
                    + "\",\"message\":\"categoryType不能为空!\"}";
                WebSocketServer.sendInfo(message, userCode);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (finalBuf.length() != 0) {
                String s = finalBuf.substring(0, finalBuf.length() - 1) + "]}";
                buf.append(s);
            } else {
                buf.append("]}");
            }
            WebSocketServer.sendInfo(buf.toString(), userCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算列表的涨跌百分比
     * 
     * @param vo
     * @return
     */
    public TransactionCategoryChildVo putTrend(TransactionCategoryChildVo vo) {
        Long dataTime = DateUtil.getReviseDate13line(null);
        Long comparisonDataTime = dataTime - 1000;
        String symbol = vo.getChildName();
        QuotationProductMo productMo = new QuotationProductMo();
        QuotationProductMo comparisonProductMo = new QuotationProductMo();
        if (cache.hasKey(
            BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + BinanceConstant.SPLIT + dataTime)) {
            Gson gson = new Gson();
            productMo = gson.fromJson((cache.get(
                BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + BinanceConstant.SPLIT + dataTime))
                    .toString(),
                QuotationProductMo.class);
        } else {
            QuotationProductVo quotationProductVo =
                orderMapper.getQuotationByStreamTime(symbol.toLowerCase(), dataTime);
            if (null == quotationProductVo) {
                vo.setUpAndDownPercent("0.00");
                vo.setUpAndDownSign(0);
                return vo;
            }
            BeanUtils.copyProperties(quotationProductVo, productMo);
        }
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + BinanceConstant.SPLIT
            + comparisonDataTime)) {
            Gson gson = new Gson();
            comparisonProductMo =
                gson.fromJson((cache.get(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase()
                    + BinanceConstant.SPLIT + comparisonDataTime)).toString(), QuotationProductMo.class);
        } else {
            QuotationProductVo quotationProductVo =
                orderMapper.getQuotationByStreamTime(symbol.toLowerCase(), comparisonDataTime);
            if (null == quotationProductVo) {
                vo.setUpAndDownPercent("0.00");
                vo.setUpAndDownSign(0);
                return vo;
            }
            BeanUtils.copyProperties(quotationProductVo, comparisonProductMo);
        }
        BigDecimal beforePrice = new BigDecimal(productMo.getClosePrice());
        BigDecimal afterPrice = new BigDecimal(comparisonProductMo.getClosePrice());
        vo.setUpAndDownPercent(getFormattedPercent(beforePrice, afterPrice));
        if (vo.getUpAndDownPercent().equals("0.00")) {
            vo.setUpAndDownSign(0);
        } else {
            vo.setUpAndDownSign(beforePrice.compareTo(afterPrice));
        }
        return vo;
    }

    private static String getFormattedPercent(BigDecimal beforePrice, BigDecimal afterPrice) {
        DecimalFormat df = new DecimalFormat("0.00");
        double reductionRate =
            100 * ((beforePrice.doubleValue() - afterPrice.doubleValue()) / afterPrice.doubleValue());
        return df.format(reductionRate).equals("-0.00") ? "0.00" : df.format(reductionRate);
    }

    @Override
    public void openKlineQuotation(String userCode, BinanceQuotationQo binanceQuotationQo) {
        List<Object> keys = cache.keys(KLINE_PREFIX + "*:*:" + userCode);
        cache.multiDel(keys);
        String dataType = binanceQuotationQo.getKlineType();
        Long endDataTime = Long.valueOf((DateUtil.getDate13line() + "").substring(0, 10) + "000");
        Long startDataTime = timeCalculation(endDataTime, binanceQuotationQo.getKlineType());
        List<QuotationKLineProductMo> listHis =
            quotationKLineProductMapper.selectHisList(binanceQuotationQo.getSymbol().toUpperCase(),
                binanceQuotationQo.getKlineType().toLowerCase(), startDataTime, endDataTime);
        StringBuffer buf = new StringBuffer("{\"dataType\":");
        buf.append(WebsocketDateEnum.K_INITIAL.getColumnType());
        buf.append(",\"requestUuid\":\"" + binanceQuotationQo.getRequestUuid());
        buf.append("\",\"data\":[");
        StringBuffer finalBuf = new StringBuffer();
        listHis.stream().forEach(item -> {
            finalBuf.append(JSONObject.toJSONString(item) + ",");
        });
        try {
            if (finalBuf.length() != 0) {
                String s = finalBuf.substring(0, finalBuf.length() - 1) + "]}";
                buf.append(s);
            } else {
                buf.append("]}");
            }
            WebSocketServer.sendInfo(buf.toString(), userCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache.put(KLINE_PREFIX + dataType + SPLIT + binanceQuotationQo.getSymbol() + SPLIT + userCode, userCode);
    }

    @Override
    public void queryHisKlineQuotation(String userCode, BinanceQuotationQo binanceQuotationQo) {
        Long startDataTime;
        Long endDataTime;
        if (binanceQuotationQo.getStartDataTime() != null && binanceQuotationQo.getEndDataTime() != null) {
            startDataTime = binanceQuotationQo.getStartDataTime();
            endDataTime = binanceQuotationQo.getEndDataTime();
        } else if (binanceQuotationQo.getStartDataTime() == null) {
            endDataTime = binanceQuotationQo.getEndDataTime();
            startDataTime = timeCalculation(endDataTime, binanceQuotationQo.getKlineType());
        } else {
            startDataTime = binanceQuotationQo.getStartDataTime();
            endDataTime = timeCalculation(startDataTime, binanceQuotationQo.getKlineType());
            endDataTime = startDataTime - endDataTime + startDataTime;
        }
        Long beforeTime = DateUtil.getReviseDate13line(null);
        if (endDataTime.longValue() > beforeTime.longValue()) {
            endDataTime = beforeTime - 1000;
        }
        List<QuotationKLineProductMo> listHis =
            quotationKLineProductMapper.selectHisList(binanceQuotationQo.getSymbol().toUpperCase(),
                binanceQuotationQo.getKlineType().toLowerCase(), startDataTime, endDataTime);
        StringBuffer buf = new StringBuffer("{\"dataType\":");
        buf.append(WebsocketDateEnum.K_HIS.getColumnType());
        buf.append(",\"requestUuid\":\"" + binanceQuotationQo.getRequestUuid());
        buf.append("\",\"data\":[");
        StringBuffer finalBuf = new StringBuffer();
        listHis.stream().forEach(item -> {
            finalBuf.append(JSONObject.toJSONString(item) + ",");
        });
        try {
            if (finalBuf.length() != 0) {
                String s = finalBuf.substring(0, finalBuf.length() - 1) + "]}";
                buf.append(s);
            } else {
                buf.append("]}");
            }
            WebSocketServer.sendInfo(buf.toString(), userCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void orderCalculation(String userCode, SettleQo settleQo) {
        Long uid = TokenUtils.getLoginUser(settleQo.getToken()).getUid();
        if (null == uid) {
            return;
        }

        List<OrderCalculationVo> list = orderMapper.orderCalculationListByUid(uid);
        if (list.size() > 0) {
            TransactionConfigMo transactionConfigMo = transactionConfigMapper.selectById(1);

            AtomicReference<Long> orderEndTime = new AtomicReference<>(DateUtil.getReviseDate13line(null));
            list.forEach(item -> {
                Long endTime = item.getEndTime();
                if (endTime.longValue() > orderEndTime.get().longValue()) {
                    orderEndTime.getAndSet(endTime);
                }
                item.setTradingStartTime(endTime - 29999);
                item.setBarStartTime(endTime - 59999);
            });
            Long beforeTime = DateUtil.getReviseDate13line(null);

            Map<Long, Map<String, List<OrderCalculationVo>>> map = list.stream().collect(Collectors
                .groupingBy(OrderCalculationVo::getEndTime, Collectors.groupingBy(OrderCalculationVo::getPairs)));
            List<OrderCalculationVo> resultList = Lists.newArrayList();

            Map<Long, Map<String, List<OrderCalculationVo>>> resultMap = sortMapByKey(map);

            for (Long key : resultMap.keySet()) {
                Map<String, List<OrderCalculationVo>> tempMap = map.get(key);
                for (String tempKey : tempMap.keySet()) {
                    resultList.addAll(tempMap.get(tempKey));
                }
            }

            UserOrderCalculationVo vo = new UserOrderCalculationVo();
            vo.setLossRatio(new BigDecimal(transactionConfigMo.getLossRatio().toString()).divide(new BigDecimal(100), 2,
                BigDecimal.ROUND_HALF_UP));
            vo.setWithdrawal(new BigDecimal(transactionConfigMo.getWithdrawal().toString()).divide(new BigDecimal(100),
                2, BigDecimal.ROUND_HALF_UP));
            vo.setOrderList(resultList);
            vo.setStreamTime(beforeTime);

            StringBuffer buf = new StringBuffer("{\"dataType\":");
            buf.append(WebsocketDateEnum.ORDER_CALCULATION.getColumnType());
            buf.append(",\"requestUuid\":\"" + settleQo.getRequestUuid());
            buf.append("\",\"data\":");
            StringBuffer finalBuf = new StringBuffer();
            Gson gson = new Gson();
            String dataString = gson.toJson(vo);
            finalBuf.append(dataString);
            try {
                if (finalBuf.length() != 0) {
                    String s = finalBuf.substring(0, finalBuf.length()) + "}";
                    buf.append(s);
                } else {
                    buf.append("}");
                }
                WebSocketServer.sendInfo(buf.toString(), userCode);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String,
                OrderCalculationVo> symbolOrderMap = resultList.stream()
                    .collect(Collectors.groupingBy(OrderCalculationVo::getPairs,
                        Collectors.collectingAndThen(
                            Collectors.reducing(
                                (c1, c2) -> c1.getEndTime().longValue() > c2.getEndTime().longValue() ? c1 : c2),
                            Optional::get)));
            if (orderEndTime.get().longValue() - beforeTime.longValue() > 0) {
                cache.put(BinanceConstant.BINANCE_QUOTATION_ORDER + userCode, symbolOrderMap,
                    orderEndTime.get().longValue() - beforeTime.longValue(), TimeUnit.MILLISECONDS);
            }

        }
    }

    @Override
    public void orderResult(String userCode, SettleQo settleQo) {
        Long uid = TokenUtils.getLoginUser(settleQo.getToken()).getUid();
        if (null == uid) {
            return;
        }
        String[] split = settleQo.getIds().split(",");
        long[] idArr = Arrays.asList(split).stream().mapToLong(Long::parseLong).toArray();
        List<Long> ids = Arrays.stream(idArr).boxed().collect(Collectors.toList());
        List<OrderMo> orderMos = orderMapper.selectBatchIds(ids);
        // 防渗透
        List<OrderMo> tempMos =
            orderMos.stream().filter(k -> k.getUid().longValue() == uid.longValue()).collect(Collectors.toList());
        if (tempMos.size() > 0) {
            Long endTime = tempMos.get(0).getEndTime() + 1;
            Long startTime = tempMos.stream().mapToLong(item -> item.getOrderTime()).max().getAsLong() - 5000;
            List<QuotationProductMo> quotationProductMos =
                quotationMapper.selectHisList(settleQo.getSymbol().toLowerCase(), startTime, endTime);
            OrderResultVo resultVo = new OrderResultVo();
            resultVo.setOrderMos(orderMos);
            resultVo.setQuotationProductMos(quotationProductMos);
            StringBuffer buf = new StringBuffer("{\"dataType\":");
            buf.append(WebsocketDateEnum.ORDER_RESULT.getColumnType());
            buf.append(",\"requestUuid\":\"" + settleQo.getRequestUuid());
            buf.append("\",\"data\":");
            StringBuffer finalBuf = new StringBuffer();
            Gson gson = new Gson();
            String dataString = gson.toJson(resultVo);
            finalBuf.append(dataString);
            try {
                if (finalBuf.length() != 0) {
                    String s = finalBuf.substring(0, finalBuf.length()) + "}";
                    buf.append(s);
                } else {
                    buf.append("}");
                }
                WebSocketServer.sendInfo(buf.toString(), userCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateOnlineCount(int onlineCount) {
        cache.put(RedisConstant.User.USER_ONLINE_NUM, onlineCount);
    }

    @Override
    public void orderRank(String userCode, OrderRankQo orderRankQo) {
        if (orderRankQo.getIsOpen().intValue() == 1) {
            cache.put(RedisConstant.Order.ORDER_RANK + orderRankQo.getSymbol() + RedisConstant.SPLIT + userCode,
                userCode);
            interposeXxlJobServer.orderRankBySymbol();
            interposeXxlJobServer.orderRank();
        } else {
            cache.remove(RedisConstant.Order.ORDER_RANK + orderRankQo.getSymbol() + RedisConstant.SPLIT + userCode);
        }
    }

    /**
     * 使用 Map按key进行排序
     * 
     * @param map
     * @return
     */
    public Map<Long, Map<String, List<OrderCalculationVo>>>
        sortMapByKey(Map<Long, Map<String, List<OrderCalculationVo>>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Long, Map<String, List<OrderCalculationVo>>> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 比较器类
     */

    class MapKeyComparator implements Comparator<Long> {
        @Override
        public int compare(Long str1, Long str2) {
            return str1.compareTo(str2);
        }
    }

    public Long timeCalculation(Long time, String timeType) {
        if (timeType.equals("5s")) {
            return time - 1000 * 5 * 70 * 2;
        } else if (timeType.equals("10s")) {
            return time - 1000 * 10 * 70 * 2;
        } else if (timeType.equals("15s")) {
            return time - 1000 * 15 * 70 * 2;
        } else if (timeType.equals("30s")) {
            return time - 1000 * 30 * 70 * 2;
        } else if (timeType.equals("1m")) {
            return time - 1000 * 60 * 70 * 2;
        } else if (timeType.equals("5m")) {
            return time - 1000 * 60 * 5 * 70 * 2;
        } else if (timeType.equals("15m")) {
            return time - 1000 * 60 * 15 * 70 * 2;
        } else if (timeType.equals("30m")) {
            return time - 1000 * 60 * 30 * 70 * 2;
        } else if (timeType.equals("1h")) {
            return time - 1000 * 60 * 60 * 70 * 2;
        } else if (timeType.equals("4h")) {
            return time - 1000 * 60 * 60 * 4 * 70 * 2;
        } else if (timeType.equals("1d")) {
            return time - 1000 * 60 * 60 * 24 * 70 * 2;
        } else if (timeType.equals("7d")) {
            return time - 1000 * 60 * 60 * 24 * 7 * 70 * 2;
        } else {
            return time - 201000;
        }
    }
}
