package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.qo.HistoryQo;
import com.ulla.modules.business.service.IMoneyHistoryService;
import com.ulla.modules.business.vo.RechargeVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "入金历史记录")
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoneyHistoryController {

    final IMoneyHistoryService iMoneyHistoryService;

    /**
     * 根据uid查询该用户的历史入金记录
     */
    @GetMapping("/page")
    @ApiOperation(value = "历史入金记录分页", notes = "历史入金记录")
    public ResultMessageVo<IPage<RechargeVo>> pageHistory(HistoryQo qo) {
        qo.setUid(UserUtil.getUid());
        Page<HistoryQo> page = new Page<>(qo.getPage(), qo.getLimit());
        return ResultUtil.data(iMoneyHistoryService.pageHistory(page, qo));
    }

}
