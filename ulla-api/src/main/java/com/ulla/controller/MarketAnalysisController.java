package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.business.service.OrderService;
import com.ulla.modules.business.vo.MarketAnalysisVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "个人资料的市场分析")
@RestController
@RequestMapping("/marketAnalysis")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MarketAnalysisController {

    final OrderService orderService;

    final UserMapper userMapper;

    /**
     * 市场分析的图形数据统计
     */
    @ApiOperation(value = "市场分析的图形数据统计", notes = "市场分析的图形数据统计")
    @GetMapping("/list")
    public ResultMessageVo<MarketAnalysisVo> marketList() {
        return ResultUtil.data(orderService.getByUid(UserUtil.getUid()));
    }
}
