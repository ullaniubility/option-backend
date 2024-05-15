package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mo.KYCMo;
import com.ulla.modules.auth.qo.KYCQo;
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

    /**
     * 验证列表
     */
    @ApiOperation("验证列表")
    @PostMapping(value = "/pageList")
    public ResultMessageVo pageList(@RequestBody KYCQo qo) {
        Page<KYCMo> page = new Page<>(qo.getPage(), qo.getPageSize());
        return ResultUtil.data(kycSerivce.pageList(page, qo));
    }

    /**
     * 通过
     */
    @ApiOperation("审核")
    @PostMapping(value = "/review")
    public ResultMessageVo review(@RequestBody KYCMo kycMo) {
        return kycSerivce.review(kycMo);
    }

}
