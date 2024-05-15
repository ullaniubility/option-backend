package com.ulla.modules.binance.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.binance.mo.ExchangeRateMo;
import com.ulla.modules.business.vo.TrendListVo;

/**
 * @author zhuyongdong
 * @Description 行情
 * @since 2023-02-27 22:50:31
 */
public interface QuotationService {

    /**
     * 根据时间间隔类型获取趋势列表
     *
     * @param trendType
     *            1。1分钟 2.2分钟 3.5分钟
     * @return
     */
    List<TrendListVo> getTrendList(Integer trendType);

    ResultMessageVo exchangeRate(String symbol);

    List<ExchangeRateMo> getExchangeRate();

    IPage<ExchangeRateMo> selectPage(PageVo pageVo);

    ExchangeRateMo updateExchangeRate(ExchangeRateMo exchangeRateMo);

    ExchangeRateMo deleteExchangeRate(ExchangeRateMo exchangeRateMo);

}
