package com.ulla.modules.binance.service.impl;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.BinanceConstant;
import com.ulla.modules.binance.mapper.ExchangeRateMapper;
import com.ulla.modules.binance.mapper.QuotationMapper;
import com.ulla.modules.binance.mo.ExchangeRateMo;
import com.ulla.modules.binance.mo.QuotationProductMo;
import com.ulla.modules.binance.service.QuotationService;
import com.ulla.modules.business.mapper.TransactionCategoryChildMapper;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.service.TransactionCategoryChildService;
import com.ulla.modules.business.vo.TrendListVo;
import com.ulla.mybatis.util.PageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuotationServiceImpl implements QuotationService {

    final Cache cache;

    final TransactionCategoryChildService transactionCategoryChildService;

    final QuotationMapper quotationMapper;

    final ExchangeRateMapper exchangeRateMapper;

    final TransactionCategoryChildMapper childMapper;

    @Override
    public List<TrendListVo> getTrendList(Integer trendType) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<TransactionCategoryChildMo>>() {}.getType();
        List<TransactionCategoryChildMo> listChild =
            gson.fromJson(cache.get("binance:api:symbolList").toString(), type);
        listChild = listChild.stream().filter(f -> f.getIsPopular().intValue() == 1).collect(Collectors.toList());
        List<TrendListVo> list = Lists.newArrayList();
        switch (trendType.intValue()) {
            case 1: {
                listChild.stream().forEach(item -> {
                    TrendListVo vo = new TrendListVo();
                    vo.setChildName(item.getChildName());
                    putTrend(vo, 1);
                    list.add(vo);
                });
                break;
            }
            case 2: {
                listChild.stream().forEach(item -> {
                    TrendListVo vo = new TrendListVo();
                    vo.setChildName(item.getChildName());
                    putTrend(vo, 2);
                    list.add(vo);
                });
                break;
            }
            case 5: {
                listChild.stream().forEach(item -> {
                    TrendListVo vo = new TrendListVo();
                    vo.setChildName(item.getChildName());
                    putTrend(vo, 5);
                    list.add(vo);
                });
                break;
            }
        }
        return list;
    }

    /**
     * 计算列表的涨跌百分比
     *
     * @param vo
     * @return
     */
    public TrendListVo putTrend(TrendListVo vo, Integer trendType) {
        String symbol = vo.getChildName().toUpperCase();
        Long fourTime = DateUtil.getReviseDate13line(null);
        Long threeTime = fourTime - 1000 * 60 * trendType;
        Long twoTime = threeTime - 1000 * 60 * trendType;
        Long oneTime = twoTime - 1000 * 60 * trendType;
        QuotationProductMo fourProductMo;
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol + BinanceConstant.SPLIT + fourTime)) {
            Gson gson = new Gson();
            fourProductMo = gson.fromJson(
                (cache.get(BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol + BinanceConstant.SPLIT + fourTime))
                    .toString(),
                QuotationProductMo.class);
        } else {
            fourProductMo = quotationMapper.selectMoByStreamTime(symbol.toLowerCase(), fourTime);
        }
        if (cache.hasKey(BinanceConstant.BINANCE_QUOTATION_TREND + symbol)) {
            Gson gson = new Gson();
            Map<String, BigDecimal> map =
                gson.fromJson(cache.get(BinanceConstant.BINANCE_QUOTATION_TREND + symbol).toString(), Map.class);

            BigDecimal onePrice = new BigDecimal(String.valueOf(map.get("onePrice")));
            BigDecimal twoPrice = new BigDecimal(String.valueOf(map.get("twoPrice")));
            BigDecimal threePrice = new BigDecimal(String.valueOf(map.get("threePrice")));
            BigDecimal fourPrice = new BigDecimal(fourProductMo.getClosePrice());
            map.put("onePrice", twoPrice);
            map.put("twoPrice", threePrice);
            map.put("threePrice", fourPrice);
            cache.put(BinanceConstant.BINANCE_QUOTATION_TREND + symbol, map);
            putVo(vo, onePrice, twoPrice, threePrice, fourPrice);
        } else {
            QuotationProductMo threeProductMo = quotationMapper.selectMoByStreamTime(symbol.toLowerCase(), threeTime);
            QuotationProductMo twoProductMo = quotationMapper.selectMoByStreamTime(symbol.toLowerCase(), twoTime);
            QuotationProductMo oneProductMo = quotationMapper.selectMoByStreamTime(symbol.toLowerCase(), oneTime);

            BigDecimal onePrice = ObjectUtils.isEmpty(oneProductMo) ? new BigDecimal("0.0")
                : new BigDecimal(oneProductMo.getClosePrice());
            BigDecimal twoPrice = ObjectUtils.isEmpty(twoProductMo) ? new BigDecimal("0.0")
                : new BigDecimal(twoProductMo.getClosePrice());
            BigDecimal threePrice = ObjectUtils.isEmpty(threeProductMo) ? new BigDecimal("0.0")
                : new BigDecimal(threeProductMo.getClosePrice());
            BigDecimal fourPrice = ObjectUtils.isEmpty(fourProductMo) ? new BigDecimal("0.0")
                : new BigDecimal(fourProductMo.getClosePrice());
            putVo(vo, onePrice, twoPrice, threePrice, fourPrice);
            Map<String, BigDecimal> map = new HashMap<>();
            map.put("onePrice", twoPrice);
            map.put("twoPrice", threePrice);
            map.put("threePrice", fourPrice);
            cache.put(BinanceConstant.BINANCE_QUOTATION_TREND + symbol, map);
        }
        return vo;
    }

    private void putVo(TrendListVo vo, BigDecimal onePrice, BigDecimal twoPrice, BigDecimal threePrice,
        BigDecimal fourPrice) {
        int i = 1;
        int one = onePrice.compareTo(twoPrice);
        int two = twoPrice.compareTo(threePrice);
        if (one == two) {
            i++;
            int three = threePrice.compareTo(fourPrice);
            if (two == three) {
                i++;
            }
        }
        vo.setUpAndDownSign(one);
        vo.setUpAndDownSignNumber(i);
    }

    @Override
    public ResultMessageVo exchangeRate(String symbol) {
        Long dataTime = DateUtil.getReviseDate13line(null);
        QuotationProductMo productMo;
        if (cache.hasKey(
            BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + BinanceConstant.SPLIT + dataTime)) {
            Gson gson = new Gson();
            productMo = gson.fromJson((cache.get(
                BinanceConstant.BINANCE_QUOTATION_CACHE_1S + symbol.toUpperCase() + BinanceConstant.SPLIT + dataTime))
                    .toString(),
                QuotationProductMo.class);
        } else {
            productMo = quotationMapper.selectMoByStreamTime(symbol.toLowerCase(), dataTime);
        }
        if (ObjectUtils.isEmpty(productMo)) {
            return ResultUtil.error(ResultCodeEnums.EXCHANGE_RATE_NOT_EXIST);
        }
        LambdaQueryWrapper<ExchangeRateMo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExchangeRateMo::getStatusFlag, 1);
        queryWrapper.eq(ExchangeRateMo::getDeleteFlag, 0);
        queryWrapper.eq(ExchangeRateMo::getChildName, symbol.toUpperCase());
        ExchangeRateMo exchangeRateMo = exchangeRateMapper.selectOne(queryWrapper);
        if (ObjectUtils.isNotEmpty(exchangeRateMo)) {
            exchangeRateMo.setExchangeRate(productMo.getClosePrice());
            exchangeRateMo.setStatusFlag(1);
            exchangeRateMapper.updateById(exchangeRateMo);
        } else {
            LambdaQueryWrapper<TransactionCategoryChildMo> childMoLambdaQueryWrapper = new LambdaQueryWrapper<>();
            childMoLambdaQueryWrapper.eq(TransactionCategoryChildMo::getChildName, productMo.getSymbol());
            TransactionCategoryChildMo childMo = childMapper.selectOne(childMoLambdaQueryWrapper);
            exchangeRateMo = new ExchangeRateMo();
            exchangeRateMo.setChildId(childMo.getId());
            exchangeRateMo.setChildName(childMo.getChildName());
            exchangeRateMo.setExchangeRate(productMo.getClosePrice());
            exchangeRateMo.setStatusFlag(1);
            exchangeRateMapper.insert(exchangeRateMo);
        }
        return ResultUtil.data(exchangeRateMo);
    }

    @Override
    public List<ExchangeRateMo> getExchangeRate() {
        return exchangeRateMapper.selectList(null);
    }

    @Override
    public IPage<ExchangeRateMo> selectPage(PageVo pageVo) {
        return exchangeRateMapper.selectPage(PageUtil.initPage(pageVo), null);
    }

    @Override
    public ExchangeRateMo updateExchangeRate(ExchangeRateMo exchangeRateMo) {
        exchangeRateMapper.updateById(exchangeRateMo);
        return exchangeRateMo;
    }

    @Override
    public ExchangeRateMo deleteExchangeRate(ExchangeRateMo exchangeRateMo) {
        exchangeRateMapper.deleteById(exchangeRateMo);
        return exchangeRateMo;
    }
}
