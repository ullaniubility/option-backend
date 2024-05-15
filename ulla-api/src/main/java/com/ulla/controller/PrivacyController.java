package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.PrivacyMo;
import com.ulla.modules.business.service.IPrivacyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "隐私政策")
@RestController
@RequestMapping("/privacy")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrivacyController {

    final IPrivacyService privacyService;

    /**
     * 客户端无分页
     */
    @ApiOperation(value = "隐私政策列表", notes = "隐私政策列表")
    @GetMapping("/list")
    public ResultMessageVo PrivacyList() {
        LambdaQueryWrapper<PrivacyMo> wrapper = new LambdaQueryWrapper<>();
        return ResultUtil.data(privacyService.list(
            wrapper.orderByDesc(PrivacyMo::getUpdateTime).eq(PrivacyMo::getStatusType, 1).eq(PrivacyMo::getForm, 0)));
    }

    @ApiOperation(value = "入金支付政策列表", notes = "入金支付政策列表")
    @GetMapping("/PayList")
    public ResultMessageVo PayList() {
        LambdaQueryWrapper<PrivacyMo> wrapper = new LambdaQueryWrapper<>();
        return ResultUtil.data(privacyService.list(
            wrapper.orderByDesc(PrivacyMo::getUpdateTime).eq(PrivacyMo::getStatusType, 1).eq(PrivacyMo::getForm, 1)));
    }
}
