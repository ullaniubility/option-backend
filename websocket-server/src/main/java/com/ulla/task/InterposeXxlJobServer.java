package com.ulla.task;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.ulla.modules.business.mo.EoPointLogMo;
import com.ulla.modules.business.service.EoPointForUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ulla.cache.Cache;
import com.ulla.common.enums.WebsocketDateEnum;
import com.ulla.common.utils.DateUtil;
import com.ulla.constant.BinanceConstant;
import com.ulla.constant.RedisConstant;
import com.ulla.modules.binance.mapper.QuotationMapper;
import com.ulla.modules.binance.mo.QuotationProductMo;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.vo.OrderCalculationVo;
import com.ulla.service.OrderAsyncTask;
import com.ulla.service.WebSocketServer;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 行情干预算法
 * @since 2023/2/23 11:41
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InterposeXxlJobServer {

    final Cache cache;

    final QuotationMapper quotationMapper;

    final OrderMapper orderMapper;

    final OrderAsyncTask orderAsyncTask;

    final EoPointForUserService eoPointForUserService;

    /**
     * 订单状态变更为生效
     */
    @XxlJob("orderAssert")
    public void orderAssert() {
        try {
            Long tradingEndTime = DateUtil.getMinuteEndTime(DateUtil.getDate13line());
            QueryWrapper<OrderMo> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("type", 2);
            queryWrapper.ne("status", 2).ne("status", 3);
            queryWrapper.eq("end_time", tradingEndTime);
            List<OrderMo> list = orderMapper.selectList(queryWrapper);
            if (list.size() > 0) {
                log.info("订单状态变更为生效");
                List<Long> idList = Lists.newArrayList();
                list.stream().forEach(item -> {
                    item.setStatus(1);
                    item.setUpdateBy(0l);
                    idList.add(item.getId());
                });
                // 批量更新订单
                UpdateWrapper<OrderMo> updateWrapper = new UpdateWrapper<>();
                updateWrapper.in("id", idList).set("status", 1);
                orderMapper.update(null, updateWrapper);
                // 当前正常订单
                list = list.stream().filter(e -> e.getType().intValue() == 1).collect(Collectors.toList());
                if (list.size() > 0) {
                    orderAsyncTask.quotationInterpose(tradingEndTime, list);
                    eoPointForUserService.calculatePoint(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订单计算
     */
    @XxlJob("orderCount")
    public void orderCount() {
        try {
            QueryWrapper<OrderMo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1);
            List<OrderMo> list = orderMapper.selectList(queryWrapper);
            if (list.size() > 0) {
                log.info("订单计算");
                list.stream().forEach(item -> {
                    item.setStatus(2);
                    item.setUpdateBy(0l);
                    item.setUpdateTime(DateUtil.getReviseDate13line(null));
                    orderAsyncTask.orderCount(item);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订单行情推送
     */
    @XxlJob("binanceQuotationOrder")
    public void binanceQuotationOrder() {
        try {
            List<Object> keys = cache.keys(BinanceConstant.BINANCE_QUOTATION_ORDER + "*");
            if (keys.size() > 0) {
                keys.stream().forEach(key -> {
                    CompletableFuture.runAsync(() -> {
                        if (cache.hasKey(key)) {
                            Map<String, OrderCalculationVo> symbolOrderMap =
                                (Map<String, OrderCalculationVo>)cache.get(key);
                            Long dataTime = DateUtil.getReviseDate13line(null);
                            String userCode = key.toString().split(":")[3];
                            List<QuotationProductMo> listQuotation = Lists.newArrayList();
                            symbolOrderMap.forEach((k, v) -> {
                                if (v.getEndTime().longValue() > dataTime.longValue()) {
                                    QuotationProductMo productMo;
                                    if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + k.toUpperCase()
                                        + BinanceConstant.SPLIT + dataTime)) {
                                        Gson gson = new Gson();
                                        productMo = gson.fromJson(
                                            (cache.get(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + k.toUpperCase()
                                                + BinanceConstant.SPLIT + dataTime)).toString(),
                                            QuotationProductMo.class);
                                    } else {
                                        productMo = quotationMapper.selectMoByStreamTime(k.toLowerCase(), dataTime);
                                    }
                                    if (null != productMo) {
                                        listQuotation.add(productMo);
                                    }
                                }
                            });
                            if (listQuotation.size() > 0) {
                                StringBuffer buf = new StringBuffer("{\"dataType\":");
                                buf.append(WebsocketDateEnum.QUOTATION_ORDER.getColumnType() + ",\"data\":");
                                StringBuffer finalBuf = new StringBuffer();
                                Gson gson = new Gson();
                                String dataString = gson.toJson(listQuotation);
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
                            } else {
                                cache.remove(key);
                            }
                        }
                    });
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 交易对订单盈利排名5秒更新一次
     */
    @XxlJob("orderRankBySymbol")
    public void orderRankBySymbol() {
        try {
            Random random = new Random();
            if (cache.hasKey(RedisConstant.User.VIRTUALLY_USER_ONLINE_NUM)) {

                int number = random.nextInt(100);
                if (random.nextInt(2) == 0) {
                    number = 0 - number;
                }
                Integer todayUserCount =
                    Integer.valueOf(cache.get(RedisConstant.User.VIRTUALLY_USER_ONLINE_NUM).toString()) + number;
                if (todayUserCount < 5) {
                    todayUserCount = 20;
                }
                cache.put(RedisConstant.User.VIRTUALLY_USER_ONLINE_NUM, todayUserCount, 24l, TimeUnit.HOURS);
            } else {
                cache.put(RedisConstant.User.VIRTUALLY_USER_ONLINE_NUM, random.nextInt(100), 24l, TimeUnit.HOURS);
            }
            if (cache.hasKey(RedisConstant.Order.VIRTUALLY_ORDER_COUNT)) {
                Integer todayOrderCount =
                    (Integer)cache.get(RedisConstant.Order.VIRTUALLY_ORDER_COUNT) + random.nextInt(20);
                cache.put(RedisConstant.Order.VIRTUALLY_ORDER_COUNT, todayOrderCount, 24l, TimeUnit.HOURS);
            } else {
                cache.put(RedisConstant.Order.VIRTUALLY_ORDER_COUNT, random.nextInt(20), 24l, TimeUnit.HOURS);
            }
            if (cache.hasKey(RedisConstant.Order.VIRTUALLY_ORDER_TOTAL_AMOUNT)) {
                Integer todayOrderCount = (Integer)cache.get(RedisConstant.Order.VIRTUALLY_ORDER_TOTAL_AMOUNT);
                Integer temp = random.nextInt(4000) + 1000;
                todayOrderCount += temp;
                cache.put(RedisConstant.Order.VIRTUALLY_ORDER_TOTAL_AMOUNT, todayOrderCount, 24l, TimeUnit.HOURS);
            } else {
                Integer temp = random.nextInt(4000) + 1000;;
                cache.put(RedisConstant.Order.VIRTUALLY_ORDER_TOTAL_AMOUNT, temp, 24l, TimeUnit.HOURS);
            }
            List<String> keys = cache.keys(RedisConstant.Order.ORDER_RANK + "*");
            if (keys.size() > 0) {
                keys.stream().forEach(key -> {
                    String userCode = cache.get(key).toString();
                    if (WebSocketServer.queryUserIsOnline(userCode)) {
                        String symbol = key.split(":")[2];
                        orderAsyncTask.orderRank(userCode, symbol);
                    } else {
                        cache.remove(key);
                    }
                });
            }
        } catch (Exception e) {
            cache.remove(RedisConstant.User.VIRTUALLY_USER_ONLINE_NUM);
            cache.remove(RedisConstant.Order.VIRTUALLY_ORDER_COUNT);
            cache.remove(RedisConstant.Order.VIRTUALLY_ORDER_TOTAL_AMOUNT);
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 订单盈利排名 1分钟更新1次
     */
    @XxlJob("orderRank")
    public void orderRank() {
        try {
            List<String> keys = cache.keys(RedisConstant.Order.ORDER_RANK + "*");
            if (keys.size() > 0) {
                keys.stream().forEach(key -> {
                    String userCode = cache.get(key).toString();
                    if (WebSocketServer.queryUserIsOnline(userCode)) {
                        orderAsyncTask.orderRank(userCode, null);
                    } else {
                        cache.remove(key);
                    }
                });
            }
        } catch (Exception e) {
            log.info("停止推送");
            e.printStackTrace();
        }
    }

    /**
     * 获取随机金额
     * 
     * @param min
     * @param max
     * @return
     */
    public static BigDecimal getRandomRedPacketBetweenMinAndMax(BigDecimal min, BigDecimal max) {
        float minF = min.floatValue();
        float maxF = max.floatValue();

        // 生成随机数
        BigDecimal db = new BigDecimal(Math.random() * (maxF - minF) + minF);

        // 返回保留两位小数的随机数。不进行四舍五入
        return db.setScale(2, BigDecimal.ROUND_DOWN);
    }
}
