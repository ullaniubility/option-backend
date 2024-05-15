package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.service.EoPointForUserService;
import com.ulla.modules.business.vo.EoPointRulesVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "eo积分相关")
@RestController
@RequestMapping("/eoPoint")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EoPointController {

    final EoPointForUserService eoPointForUserService;

    /**
     * 获取当前eo积分
     */
    @ApiOperation("获取当前eo积分")
    @GetMapping(value = "/getEoPoint")
    public ResultMessageVo getEoPoint() {
        return eoPointForUserService.getOrCreate(UserUtil.getUid());
    }

    /**
     * 获取历史记录分页
     */
    @ApiOperation("获取历史记录分页")
    @PostMapping(value = "/getLogPage")
    public ResultMessageVo getLogPage(@RequestBody EoPointRulesVo vo) {
        ResultMessageVo logPage = eoPointForUserService.getLogPage(vo, UserUtil.getUid());
        if (ObjectUtils.isEmpty(logPage.getResult())) {
            return null;
        }
        return logPage;
    }

    /**
     * 兑换奖金
     */
    @ApiOperation("兑换奖金")
    @PostMapping(value = "/transToBalance")
    public ResultMessageVo transToBalance() {
        return eoPointForUserService.transToBalance(UserUtil.getUid());
    }
}
