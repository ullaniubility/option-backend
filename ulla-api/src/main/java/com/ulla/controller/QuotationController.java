package com.ulla.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.binance.mo.ExchangeRateMo;
import com.ulla.modules.binance.service.QuotationService;
import com.ulla.modules.business.mo.SectionConfigMo;
import com.ulla.modules.business.service.SectionConfigService;
import com.ulla.modules.business.vo.SectionQuotationVo;
import com.ulla.modules.business.vo.TrendListVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 行情
 * @since 2023/3/1 13:34
 */
@Api(tags = "行情")
@Slf4j
@RestController
@RequestMapping("/quotation")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuotationController {

    final QuotationService quotationService;

    final SectionConfigService sectionConfigService;

    /**
     * 根据时间间隔类型获取趋势列表
     * 
     * @param trendType
     *            1。1分钟 2.2分钟 3.5分钟
     * @return
     */
    @ApiOperation(value = "根据时间间隔类型获取趋势列表")
    @GetMapping("/getTrendList")
    public ResultMessageVo<List<TrendListVo>> getList(Integer trendType) {
        if (null == trendType) {
            return ResultUtil.error(ResultCodeEnums.PARAMS_ERROR);
        } else if (!(trendType.intValue() == 1 || trendType.intValue() == 2 || trendType.intValue() == 5)) {
            return ResultUtil.error(ResultCodeEnums.PARAMS_ERROR);
        }
        return ResultUtil.data(quotationService.getTrendList(trendType));
    }

    /**
     * 获取结算时间点配置相对应的时间对象
     */
    @ApiOperation(value = "获取结算时间点配置相对应的时间对象", notes = "获取结算时间点配置相对应的时间对象")
    @GetMapping("/getVoById")
    public ResultMessageVo<List<SectionQuotationVo>> getVoById(@NotNull(message = "数据编号不能为空") Long id) {
        SectionConfigMo mo = sectionConfigService.getById(id);
        List<SectionQuotationVo> list = Lists.newArrayList();
        int displayNum = mo.getDisplayNumber();
        int intervalTime = mo.getIntervalTime();
        Long dataTime = DateUtil.getDate13line();
        if (intervalTime == 30) {
            for (int i = 0; i < displayNum; i++) {
                SectionQuotationVo vo = new SectionQuotationVo();
                vo.setStartTime(DateUtil.getOnMinute(dataTime) + i * 30 * 1000);
                vo.setEndTime(DateUtil.getOnMinute(dataTime) + 30 * 1000 + i * 30 * 1000 - 1);
                list.add(vo);
            }
        } else if (intervalTime == 60) {
            for (int i = 0; i < displayNum; i++) {
                SectionQuotationVo vo = new SectionQuotationVo();
                vo.setStartTime(DateUtil.getHalfMinute(dataTime) + i * 60 * 1000);
                vo.setEndTime(DateUtil.getHalfMinute(dataTime) + 30 * 1000 + i * 60 * 1000 - 1);
                list.add(vo);
            }
        }
        return ResultUtil.data(list);
    }

    @ApiOperation(value = "获取汇率列表", notes = "获取汇率列表")
    @GetMapping("/getExchangeRate")
    public ResultMessageVo<List<ExchangeRateMo>> getExchangeRate() {
        return ResultUtil.data(quotationService.getExchangeRate());
    }

}
