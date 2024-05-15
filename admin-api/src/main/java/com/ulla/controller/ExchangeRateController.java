package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.binance.mo.ExchangeRateMo;
import com.ulla.modules.binance.service.QuotationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @author zhuyongdong
 * @Description 汇率管理
 * @since 2023/5/22 10:05
 */
@Api(value = "汇率管理", tags = {"汇率管理"})
@RestController
@RequestMapping("/exchangeRate")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExchangeRateController {

    final QuotationService quotationService;

    @ApiOperation(value = "获取汇率列表", notes = "获取汇率列表")
    @GetMapping("/selectPage")
    public ResultMessageVo<IPage<ExchangeRateMo>> getExchangeRate(PageVo pageVo) {
        return ResultUtil.data(quotationService.selectPage(pageVo));
    }

    @ApiOperation(value = "编辑/禁用启用", notes = "编辑/禁用启用")
    @PostMapping("/update")
    public ResultMessageVo update(@RequestBody ExchangeRateMo exchangeRateMo) {
        return ResultUtil.data(quotationService.updateExchangeRate(exchangeRateMo));
    }

    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/delete")
    public ResultMessageVo delete(@RequestBody ExchangeRateMo exchangeRateMo) {
        return ResultUtil.data(quotationService.deleteExchangeRate(exchangeRateMo));
    }

    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    public ResultMessageVo add(@RequestBody ExchangeRateMo exchangeRateMo) {
        quotationService.exchangeRate(exchangeRateMo.getChildName());
        return ResultUtil.success();
    }

}
