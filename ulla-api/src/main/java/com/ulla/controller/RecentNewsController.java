package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.RecentNewsMo;
import com.ulla.modules.business.service.IRecentNewsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "最新动态")
@RestController
@RequestMapping("/recentNews")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecentNewsController {
    final IRecentNewsService recentNewsService;

    /**
     * 客户端不分页
     */
    @ApiOperation(value = "最新动态列表", notes = "最新动态列表")
    @GetMapping("/list")
    public ResultMessageVo RecentNewsList() {
        LambdaQueryWrapper<RecentNewsMo> wrapper = new LambdaQueryWrapper<>();
        return ResultUtil.data(recentNewsService
            .list(wrapper.orderByDesc(RecentNewsMo::getUpdateTime).eq(RecentNewsMo::getStatusType, 1)));
    }
}
