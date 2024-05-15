package com.ulla.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.qo.ConditionQo;
import com.ulla.modules.business.qo.BalanceQo;
import com.ulla.modules.business.qo.FinanceQo;
import com.ulla.modules.business.service.IMoneyHistoryService;
import com.ulla.modules.business.vo.*;
import com.ulla.modules.payment.entity.PaymentCurrencyEntity;
import com.ulla.modules.payment.mapper.PaymentCurrencyMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "入金历史记录")
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FinanceController {

    final IMoneyHistoryService iMoneyHistoryService;

    final PaymentCurrencyMapper paymentCurrencyMapper;

    /**
     * 后台财务系统的入金历史记录
     */
    @PostMapping("/page")
    @ApiOperation(value = "历史入金记录分页", notes = "历史入金记录")
    public ResultMessageVo<IPage<FinanceVo>> financeHistory(@RequestBody FinanceQo qo) {
        Page<FinanceQo> page = new Page<>(qo.getPage(), qo.getLimit());
        return ResultUtil.data(iMoneyHistoryService.financeHistory(page, qo));
    }

    /**
     * 后台财务系统的余额记录
     */
    @PostMapping("/balancePage")
    @ApiOperation(value = "余额记录分页", notes = "余额记录")
    public ResultMessageVo<IPage<BalanceVo>> balanceHistory(@RequestBody BalanceQo qo) {
        Page<BalanceQo> page = new Page<>(qo.getPage(), qo.getLimit());
        return ResultUtil.data(iMoneyHistoryService.balanceHistory(page, qo));
    }

    /**
     * 首页里的当日当周入金金额和出金金额
     */
    @GetMapping("/transaction")
    @ApiOperation(value = "入金/出金", notes = "入金/出金")
    public ResultMessageVo<ActualTransactionVo> actualTransaction() {
        return ResultUtil.data(iMoneyHistoryService.actualTransaction());
    }

    /**
     * 每种期权当日盈利金额占比
     */
    @GetMapping("/percentage")
    @ApiOperation(value = "当日盈利金额占比", notes = "当日盈利金额占比")
    public ResultMessageVo<List<HomePairsVo>> percentage() {
        return ResultUtil.data(iMoneyHistoryService.percentage());
    }

    /**
     * 收益条形图
     */
    @GetMapping("/barOrder")
    @ApiOperation(value = "首页条形图交易对每年每月的盈利", notes = "首页条形图交易对每年每月的盈利")
    public ResultMessageVo<List<YearChartVo>> barOrder(Long pairsId, String symbolNames) {
        if (ObjectUtils.isEmpty(symbolNames) && ObjectUtils.isEmpty(pairsId)) {
            return ResultUtil.data(iMoneyHistoryService.barAllOrder());
        } else {
            return ResultUtil.data(iMoneyHistoryService.barOrder(pairsId, symbolNames));
        }
    }

    /**
     * 首页最近一个月的折线图收益
     */
    @GetMapping("/barMonths")
    @ApiOperation(value = "折线图近一个月的收益", notes = "折线图近一个月的收益")
    public ResultMessageVo<List<YearChartVo>> barMonths(Long pairsId, String symbolNames) {
        if (ObjectUtils.isEmpty(symbolNames) && ObjectUtils.isEmpty(pairsId)) {
            List<YearChartVo> yearChartVos = iMoneyHistoryService.barAllMonth();
            if (ObjectUtils.isEmpty(yearChartVos)) {
                return null;
            }
            return ResultUtil.data(yearChartVos);
        } else {
            List<YearChartVo> yearChartVos = iMoneyHistoryService.barMonth(pairsId, symbolNames);
            if (ObjectUtils.isEmpty(yearChartVos)) {
                return null;
            }
            return ResultUtil.data(yearChartVos);
        }
    }

    /**
     * 首页条形图交易对每年每月的盈利
     */
    @GetMapping("/bar")
    @ApiOperation(value = "首页条形图交易对每年每月的盈利", notes = "首页条形图交易对每年每月的盈利")
    public ResultMessageVo<List<YearChartVo>> bar(String symbolNames) {
        if (ObjectUtils.isEmpty(symbolNames)) {
            return ResultUtil.data(iMoneyHistoryService.barAll());
        } else {
            String net = symbolNames.substring(0, symbolNames.lastIndexOf("/"));
            String symbol = symbolNames.substring(net.length() + 1, symbolNames.length());
            return ResultUtil.data(iMoneyHistoryService.bar(symbol, net));
        }
    }

    /**
     * 首页条形图的筛选条件
     */
    @GetMapping("/condition")
    @ApiOperation(value = "当日盈利金额占比", notes = "当日盈利金额占比")
    public ResultMessageVo<List<ConditionQo>> getCondition() {
        return ResultUtil.data(iMoneyHistoryService.getCondition());
    }

    /**
     * 出入金交易对名称
     */
    @GetMapping("/symbolName")
    @ApiOperation(value = "出入金交易对名称", notes = "出入金交易对名称")
    public ResultMessageVo<List<SymbolNameVo>> symbolName() {
        List<PaymentCurrencyEntity> paymentCurrency = paymentCurrencyMapper.getPaymentCurrency();
        ArrayList<SymbolNameVo> symbolNameVos = new ArrayList<>();
        for (PaymentCurrencyEntity entity : paymentCurrency) {
            SymbolNameVo symbolNameVo = new SymbolNameVo();
            symbolNameVo.setId(entity.getId());
            symbolNameVo.setSymbol(entity.getSymbol());
            symbolNameVo.setNet(entity.getNet());
            symbolNameVo.setSymbolNames(entity.getSymbol() + "/" + entity.getNet());
            symbolNameVos.add(symbolNameVo);
        }
        return ResultUtil.data(symbolNameVos);
    }

}
