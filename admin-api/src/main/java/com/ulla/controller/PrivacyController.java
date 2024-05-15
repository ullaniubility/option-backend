package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.PrivacyMo;
import com.ulla.modules.business.qo.PrivacyQo;
import com.ulla.modules.business.service.IPrivacyService;

import cn.hutool.core.util.StrUtil;
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
     * 隐私新增
     */
    @PostMapping("/save")
    @ApiOperation(value = "新增帮助", notes = "新增帮助")
    public ResultMessageVo save(@RequestBody PrivacyMo privacyMo) {
        if (privacyMo == null) {
            return ResultUtil.error(4002, "Parameter error");
        }
        if (StrUtil.isEmpty(privacyMo.getTitle()) || StrUtil.isEmpty(privacyMo.getContent())
            || ObjectUtils.isEmpty(privacyMo.getStatusType()) || ObjectUtils.isEmpty(privacyMo.getSort())
            || ObjectUtils.isEmpty(privacyMo.getForm())) {
            return ResultUtil.error(4002, "Parameter is empty");
        }
        if (this.privacyService.getOne(Wrappers.<PrivacyMo>lambdaQuery().eq(PrivacyMo::getTitle, privacyMo.getTitle()),
            false) != null) {
            return ResultUtil.error(4002, "Title repetition");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        privacyMo.setTitle(privacyMo.getTitle().trim());
        privacyMo.setContent(privacyMo.getContent().trim());
        privacyMo.setStatusType(privacyMo.getStatusType());
        privacyMo.setSort(privacyMo.getSort());
        privacyMo.setForm(privacyMo.getForm());
        privacyMo.setCreateTime(System.currentTimeMillis());
        privacyMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.privacyService.save(privacyMo));
    }

    /**
     * 隐私政策编辑
     */
    @PostMapping("/update")
    @ApiOperation(value = "编辑帮助", notes = "编辑帮助")
    public ResultMessageVo update(@RequestBody PrivacyMo privacyMo) {
        if (privacyMo == null || privacyMo.getId() == null) {
            return ResultUtil.error(4002, "Parameter error");
        }
        // Map<String, Object> stringObjectMap = this.tokenService.parseToken(getToken());
        // Object auditor = stringObjectMap.get(USER_ID);
        // privacy.setOperatorId(Long.valueOf(auditor.toString()));
        if (StrUtil.isNotEmpty(privacyMo.getTitle())) {
            privacyMo.setTitle(privacyMo.getTitle().trim());
        }
        if (StrUtil.isNotEmpty(privacyMo.getContent())) {
            privacyMo.setContent(privacyMo.getContent().trim());
        }
        if (ObjectUtils.isNotEmpty(privacyMo.getStatusType())) {
            privacyMo.setStatusType(privacyMo.getStatusType());
        }
        if (ObjectUtils.isNotEmpty(privacyMo.getSort())) {
            privacyMo.setSort(privacyMo.getSort());
        }
        if (ObjectUtils.isNotEmpty(privacyMo.getForm())) {
            privacyMo.setForm(privacyMo.getForm());
        }
        privacyMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.privacyService.updateById(privacyMo));
    }

    /**
     * 隐私政策删除
     */
    @ApiOperation(value = "根据主键删除隐私政策", notes = "根据id删除")
    @PostMapping("/deleteById")
    public ResultMessageVo removeById(@RequestBody PrivacyQo qo) {
        return ResultUtil.data(this.privacyService.removeById(qo.getId()));
    }

    /**
     * 隐私政策列表
     */
    @PostMapping("/page")
    @ApiOperation(value = "隐私政策列表分页", notes = "隐私政策列表")
    public ResultMessageVo<IPage<PrivacyQo>> pageList(@RequestBody PrivacyQo qo) {
        Page<PrivacyQo> page = new Page<>(qo.getPage(), qo.getLimit());
        return ResultUtil.data(privacyService.pageList(page, qo));
    }

    /**
     * 
     * 支付政策列表
     */
    @PostMapping("/payPage")
    @ApiOperation(value = "入金支付政策列表", notes = "入金支付政策列表")
    public ResultMessageVo<IPage<PrivacyQo>> payPage(@RequestBody PrivacyQo qo) {
        Page<PrivacyQo> page = new Page<>(qo.getPage(), qo.getLimit());
        return ResultUtil.data(privacyService.payPage(page, qo));
    }
}
