package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.CertificateMapper;
import com.ulla.modules.auth.mo.CertificateMo;
import com.ulla.modules.auth.qo.CertificateQo;
import com.ulla.modules.auth.service.CertificateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "验证使用证件类型")
@RestController
@RequestMapping("/certificate")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CertificateController {

    final CertificateService certificateService;

    final CertificateMapper certificateMapper;

    /**
     * 证件类型分页列表
     */
    @PostMapping("/pageList")
    @ApiOperation(value = "证件类型分页列表", notes = "证件类型分页列表")
    public ResultMessageVo<IPage<CertificateMo>> pageList(@RequestBody CertificateQo qo) {
        Page<CertificateMo> page = new Page<>(qo.getPage(), qo.getPageSize());
        return ResultUtil.data(certificateService.pageList(page, qo));
    }

    /**
     * 保存证件类型
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存证件类型", notes = "保存证件类型")
    public ResultMessageVo save(@RequestBody CertificateMo certificateMo) {
        if (StringUtils.isEmpty(certificateMo.getName()) || StringUtils.isEmpty(certificateMo.getNameEn())
            || certificateMo.getStatus() == null) {
            return ResultUtil.error(4002, "Parameter error");
        }
        if (certificateMo.getId() == null) {
            certificateService.save(certificateMo);
        } else {
            certificateMapper.updateById(certificateMo);
        }
        return ResultUtil.success(200, "Successfully saved");
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
