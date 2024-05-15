package com.ulla.modules.business.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.admin.qo.ConditionQo;
import com.ulla.modules.business.qo.BalanceQo;
import com.ulla.modules.business.qo.FinanceQo;
import com.ulla.modules.business.qo.HistoryQo;
import com.ulla.modules.business.vo.*;

public interface IMoneyHistoryService extends IService<RechargeVo> {
    IPage pageHistory(Page<HistoryQo> page, HistoryQo qo);

    IPage<FinanceVo> financeHistory(Page<FinanceQo> page, FinanceQo qo);

    IPage<BalanceVo> balanceHistory(Page<BalanceQo> page, BalanceQo qo);

    ActualTransactionVo actualTransaction();

    List<HomePairsVo> percentage();

    List<YearChartVo> bar(String symbol, String net);

    List<YearChartVo> barAll();

    List<ConditionQo> getCondition();

    List<YearChartVo> barAllOrder();

    List<YearChartVo> barOrder(Long pairsId, String symbolNames);

    List<YearChartVo> barAllMonth();

    List<YearChartVo> barMonth(Long pairsId, String symbolNames);
}
