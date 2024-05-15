package com.ulla.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.SysDocumentConfigMo;
import com.ulla.modules.admin.service.SysDocumentConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(value = "系统文档配置", tags = {"系统文档配置"})
@Slf4j
@RestController
@RequestMapping("/documentConfig")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DocumentConfigController {

    final SysDocumentConfigService documentConfigService;

    /**
     * 文档配置列表
     */
    @ApiOperation("弹窗系统菜单")
    @GetMapping(value = "/popoverList")
    public ResultMessageVo popoverList() {
        List<SysDocumentConfigMo> list = documentConfigService
            .list(new LambdaQueryWrapper<SysDocumentConfigMo>().eq(SysDocumentConfigMo::getDeleteFlag, 0));
        return ResultUtil.data(list);
    }

    /**
     * 新增文档配置
     */
    @ApiOperation("新增/编辑文档配置")
    @PostMapping(value = "/saveConfig")
    public ResultMessageVo saveConfig(@RequestBody SysDocumentConfigMo documentConfigMo) {
        return documentConfigService.saveConfig(documentConfigMo);
    }

    /**
     * 删除文档配置--逻辑删除
     */
    @ApiOperation("逻辑删除文档配置")
    @PostMapping(value = "/deleteConfig")
    public ResultMessageVo deleteConfig(@RequestBody SysDocumentConfigMo documentConfigMo) {
        return documentConfigService.deleteConfig(documentConfigMo);
    }

    /**
     * 根据key获取文档配置
     */
    @ApiOperation(value = "根据key获取文档配置", notes = "根据key获取文档配置")
    @GetMapping("/getByKey")
    public ResultMessageVo<SysDocumentConfigMo> getById(@NotNull(message = "数据编号不能为空") String configKey) {
        SysDocumentConfigMo one = documentConfigService
            .getOne(Wrappers.<SysDocumentConfigMo>query().lambda().eq(SysDocumentConfigMo::getConfigKey, configKey));
        try {
            return ResultUtil.data(documentConfigService.getById(one.getId()));
        } catch (Exception e) {
            log.info("根据key获取文档配置详情出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }
}
