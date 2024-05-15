package com.ulla.modules.business.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.DateUtil;
import com.ulla.modules.admin.qo.ConditionQo;
import com.ulla.modules.business.mapper.MoneyHistoryMapper;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.qo.BalanceQo;
import com.ulla.modules.business.qo.FinanceQo;
import com.ulla.modules.business.qo.HistoryQo;
import com.ulla.modules.business.service.IMoneyHistoryService;
import com.ulla.modules.business.vo.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class IMoneyHistoryServiceImpl extends ServiceImpl<MoneyHistoryMapper, RechargeVo>
    implements IMoneyHistoryService {

    final OrderMapper orderMapper;

    @Override
    public IPage pageHistory(Page<HistoryQo> page, HistoryQo qo) {
        // IPage<RechargeVo> rechargeVoIPage = baseMapper.selectHistory(page, qo.getUid());
        List<RechargeVo> records = baseMapper.selectHistoryList(qo.getUid());
        // List<RechargeVo> records = rechargeVoIPage.getRecords();
        Page<RechargeVo> rechargeVoPage = new Page<>();
        rechargeVoPage.setRecords(new ArrayList<>());
        rechargeVoPage.setPages(page.getPages());
        rechargeVoPage.setSize(page.getSize());
        rechargeVoPage.setCurrent(page.getCurrent());
        for (RechargeVo vo : records) {
            if (ObjectUtils.isEmpty(vo.getOrderStatus()) && ObjectUtils.isNotEmpty(vo.getRewardAmount())
                && vo.getRewardAmount().compareTo(BigDecimal.valueOf(0)) == 1) {
                RechargeVo rechargeVo = new RechargeVo();
                rechargeVo.setUid(vo.getUid());
                rechargeVo.setNet(vo.getNet());
                rechargeVo.setCreateTime(vo.getCreateTime());
                rechargeVo.setEstimatedDepositAmount(vo.getRewardAmount());
                rechargeVoPage.getRecords().add(vo);
                rechargeVoPage.getRecords().add(rechargeVo);
            } else {
                rechargeVoPage.getRecords().add(vo);
            }
        }
        rechargeVoPage.setTotal(rechargeVoPage.getRecords().size());
        List<RechargeVo> list = rechargeVoPage.getRecords().stream().skip((qo.getPage() - 1) * qo.getLimit())
            .limit(qo.getLimit()).collect(Collectors.toList());
        rechargeVoPage.setRecords(list);
        return rechargeVoPage;
    }

    @Override
    public IPage<FinanceVo> financeHistory(Page<FinanceQo> page, FinanceQo qo) {
        QueryWrapper<FinanceVo> wrapper = new QueryWrapper<>();
        wrapper.eq("l.to_user_id", 0);
        wrapper.eq("l.business_type", 1);
        wrapper.eq("l.type", 1);
        if (ObjectUtils.isNotEmpty(qo.getOrderStatus())) {
            wrapper.eq("t.order_status", qo.getOrderStatus());
        }
        if (ObjectUtils.isNotEmpty(qo.getChannelName())) {
            wrapper.eq("t.channel_name", qo.getChannelName());
        }
        if (ObjectUtils.isNotEmpty(qo.getLogNo())) {
            wrapper.like("l.log_no", qo.getLogNo());
        }
        if (ObjectUtils.isNotEmpty(qo.getMail())) {
            wrapper.like("t.mail", qo.getMail());
        }
        if (ObjectUtils.isNotEmpty(qo.getUid())) {
            wrapper.like("t.uid", qo.getUid());
        }
        if (ObjectUtils.isNotEmpty(qo.getTypeTime())) {
            if (qo.getTypeTime() == 1) {
                long timeMillis = System.currentTimeMillis();
                long beginTime = timeMillis - (timeMillis + TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
                qo.setBeginTime(beginTime);
                qo.setEndTime(beginTime + 86400000);
            }
            if (qo.getTypeTime() == 2) {
                Calendar cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                long timeInMillis = cal.getTimeInMillis();
                qo.setBeginTime(timeInMillis);
                qo.setEndTime((cal.getTime().getTime() + (7 * 24 * 60 * 60 * 1000)));
            }
            if (qo.getTypeTime() == 3) {
                Calendar cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                qo.setBeginTime(cal.getTimeInMillis());
                Calendar cal1 = Calendar.getInstance();
                cal1.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONDAY), cal1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal1.set(Calendar.DAY_OF_MONTH, cal1.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal1.set(Calendar.HOUR_OF_DAY, 24);
                qo.setEndTime(cal1.getTimeInMillis());
            }
            if (qo.getTypeTime() == 4) {
                Month month = LocalDate.now().getMonth();
                Month firstMonthOfQuarter = month.firstMonthOfQuarter();
                Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
                LocalDate beginDate = LocalDate.of(LocalDate.now().getYear(), firstMonthOfQuarter, 1);
                LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), endMonthOfQuarter,
                    endMonthOfQuarter.length(LocalDate.now().isLeapYear()));
                long beginTime = beginDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                long endTime = endDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                qo.setBeginTime(beginTime);
                qo.setEndTime(endTime);
            }
            if (qo.getTypeTime() == 5) {
                LocalDate now = LocalDate.now();
                LocalDate beginDate = LocalDate.of(now.getYear(), Month.JANUARY, 1);
                LocalDate endDate =
                    LocalDate.of(now.getYear(), Month.DECEMBER, Month.DECEMBER.length(now.isLeapYear()));
                long beginTime = beginDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                long endTime = endDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                qo.setBeginTime(beginTime);
                qo.setEndTime(endTime);
            }
            wrapper.between("l.create_time", qo.getBeginTime(), qo.getEndTime());
        }
        if (ObjectUtils.isEmpty(qo.getTypeTime()) && ObjectUtils.isNotEmpty(qo.getBeginTime())
            && ObjectUtils.isNotEmpty(qo.getEndTime())) {
            wrapper.between("l.create_time", qo.getBeginTime(), qo.getEndTime());
        }
        IPage<FinanceVo> page1 = baseMapper.financeHistory(page, wrapper);
        return page1;

    }

    @Override
    public IPage<BalanceVo> balanceHistory(Page<BalanceQo> page, BalanceQo qo) {
        QueryWrapper<BalanceVo> wrapper = new QueryWrapper<>();
        wrapper.eq("l.type", 1);
        if (ObjectUtils.isNotEmpty(qo.getBusinessType())) {
            wrapper.eq("l.business_type", qo.getBusinessType());
        }
        if (ObjectUtils.isNotEmpty(qo.getLogNo())) {
            wrapper.like("l.log_no", qo.getLogNo());
        }
        if (ObjectUtils.isNotEmpty(qo.getMail())) {
            wrapper.like("t.mail", qo.getMail());
        }
        if (ObjectUtils.isNotEmpty(qo.getUid())) {
            wrapper.like("t.uid", qo.getUid());
        }
        if (ObjectUtils.isNotEmpty(qo.getTypeTime())) {
            if (qo.getTypeTime() == 1) {
                long timeMillis = System.currentTimeMillis();
                long beginTime = timeMillis - (timeMillis + TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
                qo.setBeginTime(beginTime);
                qo.setEndTime(beginTime + 86400000);
            }
            if (qo.getTypeTime() == 2) {
                Calendar cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                long timeInMillis = cal.getTimeInMillis();
                qo.setBeginTime(timeInMillis);
                qo.setEndTime((cal.getTime().getTime() + (7 * 24 * 60 * 60 * 1000)));
            }
            if (qo.getTypeTime() == 3) {
                Calendar cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                qo.setBeginTime(cal.getTimeInMillis());
                Calendar cal1 = Calendar.getInstance();
                cal1.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONDAY), cal1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                cal1.set(Calendar.DAY_OF_MONTH, cal1.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal1.set(Calendar.HOUR_OF_DAY, 24);
                qo.setEndTime(cal1.getTimeInMillis());
            }
            if (qo.getTypeTime() == 4) {
                Month month = LocalDate.now().getMonth();
                Month firstMonthOfQuarter = month.firstMonthOfQuarter();
                Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
                LocalDate beginDate = LocalDate.of(LocalDate.now().getYear(), firstMonthOfQuarter, 1);
                LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), endMonthOfQuarter,
                    endMonthOfQuarter.length(LocalDate.now().isLeapYear()));
                long beginTime = beginDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                long endTime = endDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                qo.setBeginTime(beginTime);
                qo.setEndTime(endTime);
            }
            if (qo.getTypeTime() == 5) {
                LocalDate now = LocalDate.now();
                LocalDate beginDate = LocalDate.of(now.getYear(), Month.JANUARY, 1);
                LocalDate endDate =
                    LocalDate.of(now.getYear(), Month.DECEMBER, Month.DECEMBER.length(now.isLeapYear()));
                long beginTime = beginDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                long endTime = endDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
                qo.setBeginTime(beginTime);
                qo.setEndTime(endTime);
            }
            wrapper.between("l.create_time", qo.getBeginTime(), qo.getEndTime());
        }
        if (ObjectUtils.isEmpty(qo.getTypeTime()) && ObjectUtils.isNotEmpty(qo.getBeginTime())
            && ObjectUtils.isNotEmpty(qo.getEndTime())) {
            wrapper.between("l.create_time", qo.getBeginTime(), qo.getEndTime());
        }
        IPage<BalanceVo> balanceVoIPage = baseMapper.balanceHistory(page, wrapper);
        return balanceVoIPage;
    }

    @Override
    public ActualTransactionVo actualTransaction() {
        ActualTransactionVo actualTransactionVo = new ActualTransactionVo();
        long timeMillis = System.currentTimeMillis();
        long todayEnterBeginTime =
            timeMillis - (timeMillis + TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
        Long todayEnterEndTime = todayEnterBeginTime + 86399999;
        BigDecimal todayEnterAmount = baseMapper.actualTransaction(todayEnterBeginTime, todayEnterEndTime);
        BigDecimal todayOutAmount = baseMapper.depositMoney(todayEnterBeginTime, todayEnterEndTime);
        if (ObjectUtils.isEmpty(todayEnterAmount)) {
            actualTransactionVo.setTodayEnterAmount(BigDecimal.ZERO);
        } else {
            actualTransactionVo.setTodayEnterAmount(todayEnterAmount);
        }
        if (ObjectUtils.isEmpty(todayOutAmount)) {
            actualTransactionVo.setTodayOutAmount(BigDecimal.ZERO);
        } else {
            actualTransactionVo.setTodayOutAmount(todayOutAmount);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // start of the week
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long weekEnterBeginTime = calendar.getTimeInMillis();
        Long weekEnterEndTime = weekEnterBeginTime + (7 * 24 * 60 * 60 * 1000) - 1;
        BigDecimal weekEnter = baseMapper.selectWeekEnter(weekEnterBeginTime, weekEnterEndTime);
        BigDecimal weekOut = baseMapper.selectWeekOut(weekEnterBeginTime, weekEnterEndTime);
        if (ObjectUtils.isEmpty(weekEnter)) {
            actualTransactionVo.setWeekEnterAmount(BigDecimal.ZERO);
        } else {
            actualTransactionVo.setWeekEnterAmount(weekEnter);
        }
        if (ObjectUtils.isEmpty(weekOut)) {
            actualTransactionVo.setWeekOutAmount(BigDecimal.ZERO);
        } else {
            actualTransactionVo.setWeekOutAmount(weekOut);
        }
        Integer selectWait = baseMapper.selectWait();
        actualTransactionVo.setOutTotal(selectWait);
        Integer enterTotal = baseMapper.selectEnterWait();
        actualTransactionVo.setEnterTotal(enterTotal);
        return actualTransactionVo;
    }

    @Override
    public List<HomePairsVo> percentage() {
        List<PairVo> vos = baseMapper.percentage();
        List<PairVo> collect =
            vos.stream().filter(s -> s.getStatus().equals(2) && s.getIfProfit().equals(0) && s.getType().equals(1))
                .collect(Collectors.toList());
        for (PairVo vo : collect) {
            vo.setProfitMoney(vo.getOrderAmount());
        }
        List<PairVo> collect1 =
            vos.stream().filter(s -> s.getStatus().equals(3) && s.getIfProfit().equals(1) && s.getType().equals(1))
                .collect(Collectors.toList());
        for (PairVo vo : collect1) {
            vo.setProfitMoney(vo.getBenefit());
        }
        ArrayList<PairVo> list = new ArrayList<>();
        list.addAll(collect);
        list.addAll(collect1);
        Map<String, BigDecimal> map = list.stream().collect(Collectors.groupingBy(PairVo::getPairs,
            Collectors.reducing(BigDecimal.ZERO, PairVo::getProfitMoney, BigDecimal::add)));
        // 取map里某个字段的和
        BigDecimal reduce = list.stream().map(PairVo::getProfitMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 对map的value取前三
        List<Map.Entry<String, BigDecimal>> entryList = map.entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList());
        ArrayList<HomePairsVo> homePairsVos = new ArrayList<>();
        if (entryList.size() < 3) {
            entryList.forEach(map1 -> {
                HomePairsVo homePairsVo = new HomePairsVo();
                homePairsVo.setChildName(map1.getKey());
                homePairsVo.setPercentage(map1.getValue().divide(reduce, 5, BigDecimal.ROUND_HALF_UP));
                homePairsVos.add(homePairsVo);
            });
        } else {
            List<Map.Entry<String, BigDecimal>> entries = entryList.subList(0, 3);
            entries.forEach(map2 -> {
                HomePairsVo homePairsVo = new HomePairsVo();
                homePairsVo.setChildName(map2.getKey());
                homePairsVo.setPercentage(map2.getValue().divide(reduce, 5, BigDecimal.ROUND_HALF_UP));
                homePairsVos.add(homePairsVo);
            });
        }
        return homePairsVos;
    }

    @Override
    public List<YearChartVo> bar(String symbol, String net) {
        return baseMapper.bar(symbol, net);
    }

    @Override
    public List<YearChartVo> barAll() {
        return baseMapper.barAll();
    }

    @Override
    public List<ConditionQo> getCondition() {
        List<ConditionQo> list = baseMapper.getCondition();
        for (ConditionQo qo : list) {
            qo.setSplicing(qo.getNet() + "/" + qo.getSymbol());
        }
        return list;
    }

    @Override
    public List<YearChartVo> barAllOrder() {
        List<YearChartVo> yearChartVos = orderMapper.barAllOrder();
        for (YearChartVo vo : yearChartVos) {
            vo.setCurrencyAmount(vo.getOrderAmount().subtract(vo.getBenefit()));
        }
        return yearChartVos;
    }

    @Override
    public List<YearChartVo> barOrder(Long pairsId, String symbolNames) {
        List<YearChartVo> yearChartVos = orderMapper.barOrder(pairsId, symbolNames);
        for (YearChartVo vo : yearChartVos) {
            vo.setCurrencyAmount(vo.getOrderAmount().subtract(vo.getBenefit()));
        }
        return yearChartVos;
    }

    @Override
    public List<YearChartVo> barMonth(Long pairsId, String symbolNames) {
        long endTime = DateUtil.getDate13line();
        long startTime = DateUtil.getBeforeMonthDateline(1) * 1000;
        YearChartVo yearChartVo = new YearChartVo();
        yearChartVo.setStartTime(startTime);
        yearChartVo.setEndTime(endTime);
        yearChartVo.setPairsId(pairsId);
        yearChartVo.setSymbolNames(symbolNames);
        List<YearChartVo> yearChartVos = orderMapper.barMonth(yearChartVo);
        if (yearChartVos != null && !yearChartVos.isEmpty()) {
            yearChartVos.forEach(t -> {
                if (t.getOrderAmount() == null) {
                    t.setOrderAmount(new BigDecimal(0));
                }
                if (t.getBenefit() == null) {
                    t.setBenefit(new BigDecimal(0));
                }
                t.setCurrencyAmount(t.getOrderAmount().subtract(t.getBenefit()));
            });
            yearChartVos = getYearChartVos(endTime, startTime, yearChartVos);
        }
        return yearChartVos;
    }

    @NotNull
    private List<YearChartVo> getYearChartVos(long endTime, long startTime, List<YearChartVo> yearChartVos) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<Long> dates = getDates(sdf.format(startTime), sdf.format(endTime));
        yearChartVos.forEach(t -> {
            t.setCurrencyAmount(t.getOrderAmount().subtract(t.getBenefit()));
        });
        List<Long> removeList = new ArrayList<>();
        List<YearChartVo> removeList2 = new ArrayList<>();
        for (Long date : dates) {
            for (YearChartVo vo : yearChartVos) {
                if (date.equals(vo.getDays())) {
                    removeList.add(date);
                }
                if (vo.getDays() == null) {
                    removeList2.add(vo);
                }
            }
        }
        dates.removeAll(removeList);
        yearChartVos.removeAll(removeList2);
        List<YearChartVo> list = new ArrayList<>();
        for (Long date : dates) {
            YearChartVo vo = new YearChartVo();
            vo.setDays(date);
            vo.setCurrencyAmount(new BigDecimal(BigInteger.ZERO));
            list.add(vo);
        }
        yearChartVos.addAll(list);
        return yearChartVos.stream().sorted(Comparator.comparing(YearChartVo::getDays)).collect(Collectors.toList());
    }

    @Override
    public List<YearChartVo> barAllMonth() {
        long endTime = DateUtil.getDate13line();
        long startTime = DateUtil.getBeforeMonthDateline(1) * 1000;
        List<YearChartVo> yearChartVos = orderMapper.barAllMonth(startTime, endTime);
        return getYearChartVos(endTime, startTime, yearChartVos);
    }

    public static List<Long> getDates(String dStart, String dEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<Long> dateList = null;
        try {
            Calendar cStart = Calendar.getInstance();
            cStart.setTime(sdf.parse(dStart));
            dateList = new ArrayList<>();
            dateList.add(Long.valueOf(dStart));
            while (sdf.parse(dEnd).after(cStart.getTime())) {
                cStart.add(Calendar.DAY_OF_MONTH, 1);
                dateList.add(Long.valueOf(sdf.format(cStart.getTime())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateList;
    }

}
