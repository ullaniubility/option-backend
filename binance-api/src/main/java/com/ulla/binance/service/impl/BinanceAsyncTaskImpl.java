package com.ulla.binance.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.ulla.binance.cache.Cache;
import com.ulla.binance.mapper.QuotationMapper;
import com.ulla.binance.mo.QuotationKLineProductMo;
import com.ulla.binance.mo.QuotationProductMo;
import com.ulla.binance.service.BinanceAsyncTask;

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
public class BinanceAsyncTaskImpl implements BinanceAsyncTask {

    final Cache cache;

    final QuotationMapper quotationMapper;

    @Override
    @Async("async-executor-guava")
    public void binanceKQuotation(String symbol, List<QuotationProductMo> listQuotation) {
        try {
            List<QuotationProductMo> newList =
                listQuotation.stream().filter(distinctPredicate(m -> m.getStreamTime())).collect(Collectors.toList());
            newList.stream().forEach(item -> {
                if (quotationMapper.selectCount(symbol.toLowerCase(), item.getStreamTime()) == 0) {
                    log.debug("新增" + symbol.toLowerCase() + ":" + item);
                    quotationMapper.insertOneSeconds(symbol.toLowerCase(), item);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async("async-executor-guava")
    public void binanceKLine5SQuotation(List<QuotationKLineProductMo> listQuotation) {
        try {
            List<QuotationKLineProductMo> newList =
                listQuotation.stream().filter(distinctPredicate(m -> m.getStreamTime())).collect(Collectors.toList());
            newList.stream().forEach(item -> {
                if (quotationMapper.select5SCount(item.getSymbol().toUpperCase(), item.getStreamTime()) == 0) {
                    quotationMapper.insertFiveSeconds(item);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async("async-executor-guava")
    public void binanceKLineByInterval(QuotationKLineProductMo mo, String intervalType) {
        try {
            quotationMapper.binanceKLineByInterval(mo, intervalType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <K> Predicate<K> distinctPredicate(Function<K, Object> function) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>();
        return (t) -> null == map.putIfAbsent(function.apply(t), true);

    }

    public static void main(String[] args) {

        List<QuotationProductMo> listQuotation = Lists.newArrayList();
        QuotationProductMo temp1 = QuotationProductMo.getInstance();
        QuotationProductMo temp2 = QuotationProductMo.getInstance();
        QuotationProductMo temp3 = QuotationProductMo.getInstance();
        QuotationProductMo temp4 = QuotationProductMo.getInstance();
        temp1.setStreamTime(1l);
        temp2.setStreamTime(1l);
        temp3.setStreamTime(2l);
        temp4.setStreamTime(3l);
        listQuotation.add(temp1);
        listQuotation.add(temp2);
        listQuotation.add(temp3);
        listQuotation.add(temp4);
        List<QuotationProductMo> newList =
            listQuotation.stream().filter(distinctPredicate(m -> m.getStreamTime())).collect(Collectors.toList());
        newList.stream().forEach(item -> {
            System.out.println(item.toString());
        });

    }

}
