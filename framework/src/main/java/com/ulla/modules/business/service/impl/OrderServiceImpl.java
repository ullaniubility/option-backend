package com.ulla.modules.business.service.impl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.*;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.common.vo.exception.ServiceException;
import com.ulla.constant.BinanceConstant;
import com.ulla.constant.NumberConstant;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.mapper.BalanceLogMapper;
import com.ulla.modules.assets.mapper.BalanceMapper;
import com.ulla.modules.assets.mo.BalanceLogMo;
import com.ulla.modules.assets.mo.BalanceMo;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.auth.mo.UserLevelMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.binance.mapper.ProfitLossMapper;
import com.ulla.modules.binance.mapper.QuotationMapper;
import com.ulla.modules.binance.mo.ProfitLossMo;
import com.ulla.modules.binance.mo.QuotationMo;
import com.ulla.modules.binance.mo.QuotationProductMo;
import com.ulla.modules.binance.vo.QuotationProductVo;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mapper.TransactionConfigMapper;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.mo.TransactionConfigMo;
import com.ulla.modules.business.qo.AdminOrderQo;
import com.ulla.modules.business.qo.MarketAnalysisQo;
import com.ulla.modules.business.qo.OrderHistoryQo;
import com.ulla.modules.business.qo.OrderQo;
import com.ulla.modules.business.service.OrderService;
import com.ulla.modules.business.vo.*;
import com.ulla.mybatis.util.PageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author {clj}
 * @Description {订单service}
 * @since {2023-2-21}
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderMo> implements OrderService {

    final UserMapper userMapper;

    final OrderMapper orderMapper;

    final BalanceMapper balanceMapper;

    final BalanceLogMapper balanceLogMapper;

    final QuotationMapper quotationMapper;

    final ProfitLossMapper profitLossMapper;

    final TransactionConfigMapper transactionConfigMapper;

    final BalanceService balanceService;

    final Cache cache;

    /**
     * 下单
     *
     * @param orderVo
     *            orderVo
     * @return ResultMessageVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo order(OrderVo orderVo, Long uid) {
        TransactionConfigMo transactionConfigMo = transactionConfigMapper.selectById(1);
        if (ObjectUtils.isNotEmpty(transactionConfigMo)
            && BooleanUtils.toBoolean(transactionConfigMo.getDeleteFlag())) {
            return ResultUtil.error(ResultCodeEnums.TRANSACTION_NOT_OPENED);
        }

        // Instant start = Instant.now();
        try {
            OrderMo order = new OrderMo();
            Integer orderType = orderVo.getType();

            if (orderType.intValue() == 2) {
                // 机器人订单直接完成
                order.setStatus(2);
                orderMapper.insert(order);
                return ResultUtil.success();
            } else if (orderType.intValue() == 1) {
                LambdaQueryWrapper<OrderMo> countWrapper = new LambdaQueryWrapper();
                countWrapper.eq(OrderMo::getUid, uid);
                countWrapper.and(wrapper -> wrapper.eq(OrderMo::getStatus, 1).or().eq(OrderMo::getStatus, 0));
                countWrapper.eq(OrderMo::getType, 1);
                long countOrder = orderMapper.selectCount(countWrapper);
                Gson gson = new Gson();
                Type type = new TypeToken<List<UserLevelMo>>() {}.getType();
                List<UserLevelMo> list = gson.fromJson(cache.get("user:level:list").toString(), type);
                UserMo userMo = userMapper.selectById(uid);

                UserLevelMo userLevelMo =
                    list.stream().filter(v -> v.getLevel().equals(userMo.getUserLevel())).findFirst().orElse(null);
                if (countOrder >= Long.valueOf(userLevelMo.getQuotaNum()).intValue()) {
                    return ResultUtil.error(30318,
                        "超过最大下单数量,目前最大同时下单数量为：" + userLevelMo.getQuotaNum() + ",提升会员等级可以提高同时下单数量,具体详情请咨询客服。");
                }
                if (orderVo.getOrderAmount().compareTo(new BigDecimal(userLevelMo.getQuotaPrice())) > 0) {
                    return ResultUtil.error(30319,
                        "超过单笔最大交易金额,目前单笔最大交易金额为：" + userLevelMo.getQuotaPrice() + ",提升会员等级可以提高单笔最大交易金额,具体详情请咨询客服。");
                }
            } else if (orderType.intValue() == 0) {
                LambdaQueryWrapper<OrderMo> countWrapper = new LambdaQueryWrapper();
                countWrapper.eq(OrderMo::getUid, uid);
                countWrapper.and(wrapper -> wrapper.eq(OrderMo::getStatus, 1).or().eq(OrderMo::getStatus, 0));
                countWrapper.eq(OrderMo::getType, 0);
                long countOrder = orderMapper.selectCount(countWrapper);
                if (countOrder > 20) {
                    return ResultUtil.error(ResultCodeEnums.ORDER_COUNT_MAX_ERROR);
                }
            }

            long millis = System.currentTimeMillis();
            Long streamTime = orderVo.getStreamTime();
            if (millis - streamTime > 30000) {
                return ResultUtil.error(ResultCodeEnums.ORDER_NET_ERROR);
            }
            BeanUtil.copyProperties(orderVo, order);
            /*if (millis > streamTime.longValue()) {
                millis = millis % 1000 + streamTime - 1000;
            }*/
            String orderCode = "#" + IdUtils.get8SimpleUUID();
            order.setOrderTime(streamTime);
            order.setStreamTime(streamTime);
            order.setOrderCode(orderCode);
            order.setUid(uid);
            order.setCreateBy(uid);
            order.setCreateTime(millis);
            order.setPairsId(orderVo.getPairsId());
            Long endTime = 0l;
            String symbol = orderVo.getPairs().toLowerCase();
            QuotationProductMo productMo = new QuotationProductMo();
            if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + BinanceConstant.SPLIT
                + streamTime)) {
                Gson gson = new Gson();
                productMo = gson.fromJson((cache.get(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase()
                    + BinanceConstant.SPLIT + streamTime)).toString(), QuotationProductMo.class);
            } else {
                QuotationProductVo quotationProductVo =
                    orderMapper.getQuotationByStreamTime(symbol.toLowerCase(), streamTime);
                if (null == quotationProductVo) {
                    return ResultUtil.error(4002, "The data is illegal and has been recorded.");
                }
                BeanUtils.copyProperties(quotationProductVo, productMo);
            }
            order.setPrice(productMo.getClosePrice());
            if (orderVo.getStatus().intValue() == 4) {
                endTime = Long.valueOf(orderVo.getTradingRange()) + 29999;
                order.setEndTime(endTime);
            } else {
                order.setStatus(NumberConstant.ZERO);
                order.setEndTime(productMo.getTradingEndTime());
            }
            // Instant startPlaceOrder = Instant.now();
            /*Instant finishPlaceOrder = Instant.now();
            long timeElapsed = Duration.between(startPlaceOrder, finishPlaceOrder).toMillis();
            log.info("PlaceOrder {} 毫秒", timeElapsed);
            Instant finish = Instant.now();
            long timeEnd = Duration.between(start, finish).toMillis();
            log.info("下单耗时 {} 毫秒", timeEnd);*/
            return this.placeOrder(order);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(500, "Failed to place an order");
        }
    }

    @Override
    public ResultMessageVo placeOrder(OrderMo order) {
        if (order.getOrderAmount() == null) {
            return ResultUtil.error(500, "Abnormal order amount!");
        }
        BigDecimal orderAmount = order.getOrderAmount();

        BalanceChangeVo balanceChangeVo = new BalanceChangeVo();
        balanceChangeVo.setUid(order.getUid());
        balanceChangeVo.setBusinessNo(order.getOrderCode());
        balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.EXCHANGE);
        switch (order.getType()) {
            case 0: {
                balanceChangeVo.setVirtualAmount(BigDecimal.ZERO.subtract(order.getOrderAmount()));
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1: {
                BalanceMo realBalance = balanceMapper.selectByUserId(order.getUid(), 1);
                if (null != orderAmount && orderAmount.compareTo(realBalance.getBalance()) > 0) {
                    BalanceMo bonusBalance = balanceMapper.selectByUserId(order.getUid(), 0);
                    BigDecimal totalBalance = realBalance.getBalance().compareTo(new BigDecimal("0.0")) > 0
                        ? realBalance.getBalance().add(bonusBalance.getBalance()) : bonusBalance.getBalance();
                    // 余额不足直接返回
                    if (orderAmount.compareTo(totalBalance) > 0) {
                        return ResultUtil.error(ResultCodeEnums.WALLET_INSUFFICIENT);
                    } else {
                        if (realBalance.getBalance().compareTo(new BigDecimal("00000000000000000.00")) > 0) {
                            orderAmount = orderAmount.subtract(realBalance.getBalance());
                            order.setWithdrawalAmount(realBalance.getBalance());
                            balanceChangeVo.setAmount(BigDecimal.ZERO.subtract(realBalance.getBalance()));
                        }
                        balanceChangeVo.setBonusAmount(BigDecimal.ZERO.subtract(orderAmount));
                    }
                } else if (null != orderAmount) {
                    order.setWithdrawalAmount(orderAmount);
                    balanceChangeVo.setAmount(BigDecimal.ZERO.subtract(orderAmount));
                }
                break;
            }
        }
        order.setIpAddress(UserUtil.getIp());
        orderMapper.insert(order);
        return balanceService.transactionChangeBalanceAndSaveLog(balanceChangeVo);
    }

    /**
     * 删除订单
     *
     * @param orderId
     *            orderId
     * @return ResultMessageVo
     */
    @Override
    public ResultMessageVo deleteOrder(Long orderId) {
        try {
            OrderMo orderMo = orderMapper.selectById(orderId);
            orderMo.setDeleteFlag(NumberConstant.ONE);
            orderMapper.updateById(orderMo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    @Override
    public IPage<OrderSearchVo> simulationOrder(Page<OrderQo> page, OrderQo qo) {
        IPage<OrderSearchVo> orderVoIPage = baseMapper.simulationOrder(page, qo);
        List<OrderSearchVo> records = orderVoIPage.getRecords();
        if (ObjectUtils.isEmpty(records)) {
            IPage<OrderSearchVo> vo = new Page<OrderSearchVo>();
            return vo;
        } else {
            // 1.先按时间分组，再按区间分组，最后按交易对分组
            Map<Long,
                Map<String, Map<String, List<OrderSearchVo>>>> collect1 = records.stream()
                    .filter(map -> map.getOrderTime() != null).filter(map1 -> map1.getTradingRange() != null)
                    .filter(map2 -> map2.getPairs() != null)
                    .collect(Collectors.groupingBy(OrderSearchVo::getOrderTime, Collectors
                        .groupingBy(OrderSearchVo::getTradingRange, Collectors.groupingBy(OrderSearchVo::getPairs))));
            collect1.forEach((k, v) -> {
                Map<String, Map<String, List<OrderSearchVo>>> map = v;
                map.forEach((k1, v1) -> {
                    Map<String, List<OrderSearchVo>> listMap = v1;
                    listMap.forEach((k2, v2) -> {
                        // List<OrderSearchVo> vos = v2;
                        // 区间总盈利
                        // OrderSearchVo vo = new OrderSearchVo();
                        BigDecimal totalBenefit =
                            v2.stream().map(OrderSearchVo::getBenefit).reduce(BigDecimal.ZERO, BigDecimal::add);
                        // 区间下单总金额
                        BigDecimal totalPrice =
                            v2.stream().map(OrderSearchVo::getOrderAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        // 小于等于0就等于区交易亏了
                        if (totalBenefit.compareTo(BigDecimal.ZERO) == 0
                            || totalBenefit.compareTo(BigDecimal.ZERO) == -1) {
                            Integer totalProfit = 0;
                            v2.stream().forEach(v3 -> {
                                v3.setTotalProfit(totalProfit);
                                v3.setTotalBenefit(totalBenefit);
                                v3.setTotalPrice(totalPrice);
                            });
                        }
                        // 大于0就证明区交易赚了
                        if (totalBenefit.compareTo(BigDecimal.ZERO) == 1) {
                            Integer totalProfit = 1;
                            v2.stream().forEach(v3 -> {
                                v3.setTotalProfit(totalProfit);
                                v3.setTotalBenefit(totalBenefit);
                                v3.setTotalPrice(totalPrice);
                            });
                        }
                    });
                });
            });
            for (OrderSearchVo vo : records) {
                String pairs = vo.getPairs();
                Long orderTime = vo.getOrderTime();
                List<QuotationMo> quotationMos =
                    quotationMapper.selectByRange(pairs.toLowerCase(), orderTime - 15000, orderTime + 35000);
                List<OrderSearchVo.Quotation> quotations = new ArrayList<>();
                quotationMos.stream().forEach(item -> {
                    OrderSearchVo.Quotation quotationMo = new OrderSearchVo.Quotation();
                    quotationMo.setQuotationTime(item.getStreamTime());
                    quotationMo.setQuotationPrice(item.getClosePrice());
                    quotations.add(quotationMo);
                });
                vo.setQuotation(quotations);
            }
        }
        return orderVoIPage;
    }

    @Override
    public MarketAnalysisVo getByUid(Long uid) {
        // 1.查找该用户在当前时间的一个月内的所有订单记录
        MarketAnalysisQo marketAnalysisQo = new MarketAnalysisQo();
        marketAnalysisQo.setUid(uid);;
        long endTime = DateUtil.getDate13line();
        marketAnalysisQo.setEndTime(endTime);
        long beginTime = DateUtil.getBeforeMonthDateline(1);
        marketAnalysisQo.setBeginTime(beginTime * 1000);
        // 下面是获得条形图的赢取交易比，交易量和交易笔数
        MarketAnalysisVo marketAnalysisVos = baseMapper.selectMonth(marketAnalysisQo);
        // 获取当天日期及一个月以前的日期
        // 结束时间为今天的日期
        String overTime = DateUtil.toString(endTime / 1000, "yyyy-MM-dd");
        // 开始时间为过去23天的日期
        Map<String, Object> yearMonthAndDay = DateUtil.getYearMonthAndDay(23);
        String startTime =
            yearMonthAndDay.get("year") + "-" + yearMonthAndDay.get("month") + "-" + yearMonthAndDay.get("day");
        List<String> days = baseMapper.selectTime(startTime, overTime);
        // 这是拿到一个月内的所有的订单时间和下单总金额
        marketAnalysisQo.setStartTime(startTime);
        marketAnalysisQo.setOverTime(overTime);
        List<MarketAnalysisVo.Transaction> transactions = baseMapper.selectTransaction(marketAnalysisQo);
        marketAnalysisVos.setTransactions(transactions);
        // 取根据交易对分组求和，倒序排列
        List<MarketAnalysisVo.MarketAnalysis> marketAnalyses = baseMapper.selectCountPairs(marketAnalysisQo);
        Integer allCount = baseMapper.selectAllCount(marketAnalysisQo);
        for (MarketAnalysisVo.MarketAnalysis marketAnalysis : marketAnalyses) {
            BigDecimal rate = new BigDecimal(marketAnalysis.getPairCount()).divide(new BigDecimal(allCount), 5,
                BigDecimal.ROUND_DOWN);
            marketAnalysis.setPercent(rate);
        }
        marketAnalysisVos.setMarketAnalysisList(marketAnalyses);
        return marketAnalysisVos;
    }

    /**
     * 撤销订单
     *
     * @param orderId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo closeOrder(Long orderId) {
        OrderMo orderMo = orderMapper.selectById(orderId);
        if (null == orderMo) {
            return ResultUtil.error(ResultCodeEnums.ORDER_ITEM_NOT_EXIST);
        }
        Map<String, String> closePositionMap = (Map<String, String>)cache.get("user:closePosition:map");
        String max = closePositionMap.get("closePositionMax");
        String min = closePositionMap.get("closePositionMin");

        Long beforeTime = DateUtil.getReviseDate13line(null);
        Long orderTime = DateUtil.getReviseDate13line(orderMo.getOrderTime());
        Long startTime = orderMo.getEndTime() - 29999;

        // 总亏损时间
        long lossTime = (startTime - orderTime) / 1000;
        // 已亏损时间
        long lossIncurredTime = (beforeTime - orderTime) / 1000;
        // 下单金额
        BigDecimal orderAmount = orderMo.getOrderAmount();
        // 即扣除后金额
        BigDecimal wq = orderAmount.multiply(new BigDecimal(max));
        // 最小撤单剩余金额
        BigDecimal wf = orderAmount.multiply(new BigDecimal(min));
        // 可亏损金额
        BigDecimal sh = wq.subtract(wf);
        // 平均亏损金额
        BigDecimal gf = sh.divide(new BigDecimal(lossTime), 10, BigDecimal.ROUND_DOWN);
        // 已亏损金额
        BigDecimal incurredPrice = gf.multiply(new BigDecimal(lossIncurredTime));
        // 剩余金额
        BigDecimal surplusPrice = wq.subtract(incurredPrice).setScale(2, BigDecimal.ROUND_DOWN);
        // 最终亏损金额
        BigDecimal resultPrice = orderAmount.subtract(surplusPrice).setScale(2, BigDecimal.ROUND_DOWN);
        orderMo.setIfProfit(0);
        orderMo.setBenefit(surplusPrice);
        orderMo.setStatus(3);
        // 金额回退
        BalanceChangeVo balanceChangeVo = new BalanceChangeVo();
        balanceChangeVo.setUid(orderMo.getUid());
        if (orderMo.getType().intValue() == 1) {

            // orderMo.getOrderCode()
            LambdaQueryWrapper<BalanceLogMo> logWrapper = new LambdaQueryWrapper<>();
            logWrapper.eq(BalanceLogMo::getBusinessNo, orderMo.getOrderCode());
            List<BalanceLogMo> logList = balanceLogMapper.selectList(logWrapper);

            if (logList.size() == 0) {
                throw new ServiceException(ResultCodeEnums.ORDER_NOT_EXIST);
            } else {
                BalanceLogMo bonusLogMo = null;
                if (logList.stream().filter(k -> k.getType().intValue() == 0).count() > 0) {
                    bonusLogMo = logList.stream().filter(k -> k.getType().intValue() == 0).findFirst().get();
                }
                // 计入统计
                Long todayTime = DateUtil.getTodayStartTime();
                ProfitLossMo profitLossMo = profitLossMapper.selectById(todayTime);
                if (null != bonusLogMo) {
                    BigDecimal bonusAmount = bonusLogMo.getAmount();
                    if (bonusAmount.subtract(orderAmount).compareTo(new BigDecimal("0.0")) != 0) {
                        BigDecimal bonusSurplusPrice =
                            surplusPrice.multiply((bonusAmount.divide(orderAmount, 10, BigDecimal.ROUND_DOWN)));
                        balanceChangeVo.setBonusAmount(bonusSurplusPrice);
                        balanceChangeVo.setAmount(surplusPrice.subtract(bonusSurplusPrice));
                        if (null == profitLossMo) {
                            profitLossMo = new ProfitLossMo();
                            profitLossMo.setId(todayTime);
                            profitLossMo.setProfit(
                                orderAmount.subtract(bonusAmount).subtract(balanceChangeVo.getAmount()).toString());
                            profitLossMo.setLoss("0.0");
                            profitLossMapper.insert(profitLossMo);
                        } else {
                            profitLossMo.setProfit(new BigDecimal(profitLossMo.getProfit())
                                .add(orderAmount.subtract(bonusAmount).subtract(balanceChangeVo.getAmount()))
                                .toString());
                            profitLossMapper.updateById(profitLossMo);
                        }
                    } else {
                        balanceChangeVo.setBonusAmount(surplusPrice);
                    }
                } else {
                    if (null == profitLossMo) {
                        profitLossMo = new ProfitLossMo();
                        profitLossMo.setId(todayTime);
                        profitLossMo.setProfit(resultPrice.toString());
                        profitLossMo.setLoss("0.0");
                        profitLossMapper.insert(profitLossMo);
                    } else {
                        profitLossMo.setProfit(new BigDecimal(profitLossMo.getProfit()).add(resultPrice).toString());
                        profitLossMapper.updateById(profitLossMo);
                    }
                    balanceChangeVo.setAmount(surplusPrice);
                }
                balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.CLOSE_ORDER);
                balanceChangeVo.setBusinessNo(orderMo.getOrderCode());
                balanceService.transactionChangeBalanceAndSaveLog(balanceChangeVo);
            }

        } else if (orderMo.getType().intValue() == 0) {
            // 金额回退
            balanceChangeVo.setVirtualAmount(surplusPrice);
            balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.CLOSE_ORDER);
            balanceChangeVo.setBusinessNo(orderMo.getOrderCode());
            balanceService.transactionChangeBalanceAndSaveLog(balanceChangeVo);
        }

        orderMo.setUpdateBy(orderMo.getUid());
        orderMo.setUpdateTime(beforeTime);
        orderMapper.updateById(orderMo);

        List<QuotationProductMo> quotationProductMos =
            quotationMapper.selectHisList(orderMo.getPairs().toLowerCase(), orderTime - 5000, beforeTime + 1000);
        CloseOrderVo closeOrderVo = new CloseOrderVo();
        closeOrderVo.setId(orderId);
        closeOrderVo.setOrderCode(orderMo.getOrderCode());
        closeOrderVo.setPrice(orderMo.getPrice());
        closeOrderVo.setPairs(orderMo.getPairs());
        closeOrderVo.setTradingEndTime(orderMo.getEndTime());
        closeOrderVo.setOpenClose(orderMo.getOpenClose());
        closeOrderVo.setOrderTime(orderTime);
        closeOrderVo.setOrderAmount(orderAmount);
        closeOrderVo.setSurplusPrice(surplusPrice);
        closeOrderVo.setUpdateTime(beforeTime);
        closeOrderVo.setQuotationProductMos(quotationProductMos);
        Map<String, Object> map = new HashMap<>();
        map.put("closeOrderVo", closeOrderVo);
        map.put("userBalance", balanceMapper.getWallet(orderMo.getUid()));
        return ResultUtil.data(closeOrderVo);
    }

    @Override
    public IPage<OrderMo> adminOrderListByPage(AdminOrderQo adminOrderQo) {
        if (StringUtils.isNotBlank(adminOrderQo.getOpenId())) {
            adminOrderQo.setUid(userMapper.getUserIdByOpenId(adminOrderQo.getOpenId()));
        }
        return orderMapper.selectPage(PageUtil.initPage(adminOrderQo), adminOrderQo.queryWrapper());
    }

    @Override
    public ResultMessageVo listByPage(Integer pageNumber, Integer pageSize, Integer dataType, Long uid) {
        if (dataType == null) {
            return ResultUtil.error(ResultCodeEnums.PARAMS_ERROR);
        }
        if (uid == null) {
            return ResultUtil.error(ResultCodeEnums.USER_NOT_LOGIN);
        }
        PageVo pageVo = new PageVo();
        pageVo.setPageNumber(pageNumber);
        pageVo.setPageSize(pageSize);
        QueryWrapper<OrderMo> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("status", 2).or().eq("status", 3));
        queryWrapper.eq("type", dataType);
        queryWrapper.eq("uid", uid);
        queryWrapper.orderByDesc("order_time");
        IPage<OrderMo> list = orderMapper.selectPage(PageUtil.initPage(pageVo), queryWrapper);
        List<OrderHistoryVo> voList = list.getRecords().stream().map(orderMo -> {
            OrderHistoryVo orderHistoryVo = new OrderHistoryVo();
            orderHistoryVo.setId(orderMo.getId());
            orderHistoryVo.setOrderAmount(orderMo.getOrderAmount());
            orderHistoryVo.setPairs(orderMo.getPairs());
            orderHistoryVo.setOrderTime(DateUtil.toString(orderMo.getOrderTime(), "HH:mm"));
            orderHistoryVo.setEndDate(DateUtil.toString(orderMo.getOrderTime(), "yyyy-MM-dd"));
            orderHistoryVo.setOpenClose(orderMo.getOpenClose());
            orderHistoryVo.setEndPrice(orderMo.getEndPrice());
            orderHistoryVo.setEndTime(orderMo.getEndTime().toString());
            orderHistoryVo.setTotalByTime(orderMo.getBenefit());
            orderHistoryVo.setStatus(orderMo.getStatus());
            return orderHistoryVo;
        }).sorted(Comparator.comparing(OrderHistoryVo::getId).reversed()).collect(Collectors.toList());
        return ResultUtil.data(PageUtil.convertPage(list, voList));
    }

    @Override
    public ResultMessageVo page(OrderHistoryQo orderHistoryQo, Long uid) {
        if (uid == null) {
            return ResultUtil.error(ResultCodeEnums.USER_NOT_LOGIN);
        }
        orderHistoryQo.setUid(uid);
        IPage<OrderHistoryVo> pageList =
            orderMapper.recentTransactions(PageUtil.initPage(orderHistoryQo), orderHistoryQo.queryWrapper());
        List<OrderHistoryVo> orderHistoryVoList = pageList.getRecords();
        List<OrderHistoryResultVo> list = orderHistoryVoList.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(vo -> vo.getEndDate()))),
                ArrayList::new))
            .stream().map(k -> {
                OrderHistoryResultVo resultVo = new OrderHistoryResultVo();
                resultVo.setEndDate(k.getEndDate());
                resultVo.setOrderCountByDate(k.getOrderCountByDate());
                resultVo.setTotalByDate(k.getTotalByDate());
                resultVo.setEndDateStamp(DateUtil.getDate13line(k.getEndDate() + " 00:00:00"));
                String endDate = k.getEndDate();
                List<OrderHistoryGroupTimeVo> groupTimeList =
                    orderHistoryVoList.stream().filter(v -> endDate.equals(v.getEndDate()))
                        .collect(Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(vo -> vo.getEndTime()))),
                            ArrayList::new))
                        .stream().map(temp -> {
                            OrderHistoryGroupTimeVo groupTime = new OrderHistoryGroupTimeVo();
                            groupTime.setPairs(temp.getPairs());
                            groupTime.setTotalByTime(temp.getTotalByTime());
                            groupTime.setEndTime(temp.getEndTime());
                            groupTime.setOrderCountByTime(temp.getOrderCountByTime());
                            groupTime.setEndPrice(temp.getEndPrice());
                            groupTime.setEndTimeStamp(temp.getEndTimeStamp());
                            String endTime = temp.getEndTime();
                            List<OrderHistoryResultListVo> orderVos = orderHistoryVoList.stream()
                                .filter(j -> endDate.equals(j.getEndDate()) && j.getEndTime().equals(endTime))
                                .collect(Collectors.toList()).stream().map(orderVosTemp -> {
                                    OrderHistoryResultListVo orderVo = new OrderHistoryResultListVo();
                                    orderVo.setId(orderVosTemp.getId());
                                    orderVo.setOrderCode(orderVosTemp.getOrderCode());
                                    orderVo.setPrice(orderVosTemp.getPrice());
                                    orderVo.setOrderAmount(orderVosTemp.getOrderAmount());
                                    orderVo.setOpenClose(orderVosTemp.getOpenClose());
                                    orderVo.setOrderTime(orderVosTemp.getOrderTime());
                                    orderVo.setOrderTimeStamp(orderVosTemp.getOrderTimeStamp());
                                    orderVo.setBenefit(orderVosTemp.getBenefit());
                                    return orderVo;
                                }).sorted(Comparator.comparing(OrderHistoryResultListVo::getOrderTime).reversed())
                                .collect(Collectors.toList());
                            groupTime.setListOrder(orderVos);
                            return groupTime;
                        }).sorted(Comparator.comparing(OrderHistoryGroupTimeVo::getEndTime).reversed()
                            .thenComparing(OrderHistoryGroupTimeVo::getPairs))
                        .collect(Collectors.toList());
                resultVo.setListGroupTime(groupTimeList);
                return resultVo;
            }).sorted(Comparator.comparing(OrderHistoryResultVo::getEndDate).reversed()).collect(Collectors.toList());
        IPage iPage = PageUtil.convertPage(pageList, list);
        return ResultUtil.data(iPage);
    }

    @Override
    public ResultMessageVo getDetailById(String orderIds, Long uid) {
        if (uid == null) {
            return ResultUtil.error(ResultCodeEnums.USER_NOT_LOGIN);
        }
        List<Long> idList = Arrays.stream(orderIds.split(",")).map(k -> {
            return Long.valueOf(k);
        }).collect(Collectors.toList());
        QueryWrapper<OrderMo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        queryWrapper.eq("uid", uid);
        List<OrderMo> list = orderMapper.selectList(queryWrapper);
        if (list.size() == 0) {
            return ResultUtil.data(null);
        }
        OrderMo orderMo = list.get(0);
        Long endTime = orderMo.getEndTime() + 1;
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setTradingTime(DateUtil.toString(endTime, "HH:mm:ss"));
        orderDetailVo.setTradingTimeStamp(endTime);
        orderDetailVo.setTradingPrice(orderMo.getEndPrice());
        AtomicReference<BigDecimal> totalOrderAmount = new AtomicReference<>(new BigDecimal("0.0"));
        AtomicReference<BigDecimal> profitPrice = new AtomicReference<>(new BigDecimal("0.0"));
        List<OrderDetailVo.OrderVo> orderVos = list.stream().map(orderTemp -> {
            OrderDetailVo.OrderVo orderVo = new OrderDetailVo.OrderVo();
            orderVo.setBenefit(orderTemp.getBenefit());
            orderVo.setOrderAmount(orderTemp.getOrderAmount());
            orderVo.setOrderTime(DateUtil.toString(orderTemp.getOrderTime(), "HH:mm:ss"));
            orderVo.setOrderTimeStamp(DateUtil.getReviseDate13line(orderTemp.getOrderTime()));
            orderVo.setPrice(orderTemp.getPrice());
            orderVo.setOpenClose(orderTemp.getOpenClose());
            orderVo.setUpdateTime(DateUtil.getReviseDate13line(orderTemp.getUpdateTime()));
            totalOrderAmount.getAndSet(totalOrderAmount.get().add(orderTemp.getOrderAmount()));
            if (orderTemp.getBenefit().compareTo(profitPrice.get()) > 0) {
                profitPrice.getAndSet(profitPrice.get().add(orderTemp.getBenefit()));
            }
            return orderVo;
        }).sorted(Comparator.comparing(OrderDetailVo.OrderVo::getOrderTime).reversed()).collect(Collectors.toList());
        orderDetailVo.setOrderVos(orderVos);
        orderDetailVo.setTotalOrderAmount(totalOrderAmount.get());
        orderDetailVo.setProfitPrice(profitPrice.get());
        Long orderTime = DateUtil.getReviseDate13line(orderMo.getOrderTime());
        List<QuotationProductMo> quotationProductMos =
            quotationMapper.selectHisList(orderMo.getPairs().toLowerCase(), orderTime - 5000, endTime + 6000);
        orderDetailVo.setQuotationProductMos(quotationProductMos);
        return ResultUtil.data(orderDetailVo);
    }

}
