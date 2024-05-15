package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mo.KYCMo;
import com.ulla.modules.auth.service.CertificateService;
import com.ulla.modules.auth.service.KYCSerivce;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "用户信息验证")
@RestController
@RequestMapping("/kyc")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KYCController {

    final KYCSerivce kycSerivce;

    final CertificateService certificateService;

    /**
     * 验证列表
     */
    @ApiOperation("验证列表")
    @PostMapping(value = "/startVerify")
    public ResultMessageVo startVerify(@RequestBody KYCMo kycMo) {
        return ResultUtil.data(kycSerivce.save(kycMo));
    }

    /**
     * 获取证件类型列表
     */
    @GetMapping("/getList")
    @ApiOperation(value = "获取证件类型列表", notes = "获取证件类型列表")
    public ResultMessageVo getList() {
        return certificateService.getList();
    }
}
