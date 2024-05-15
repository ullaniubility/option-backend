package com.ulla.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.SysDocumentConfigMo;
import com.ulla.modules.admin.service.SysDocumentConfigService;
import com.ulla.modules.business.mo.LearningMaterialsMo;
import com.ulla.modules.business.service.ILearningMaterialsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "学习资料")
@RestController
@Slf4j
@RequestMapping("/learningMaterials")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LearningMaterialsController {

    final ILearningMaterialsService learningMaterialsService;

    final SysDocumentConfigService documentConfigService;

    /**
     * 客户端无分页
     */
    @ApiOperation(value = "学习资料列表", notes = "学习资料列表")
    @GetMapping("/list")
    public ResultMessageVo LearningMaterialsList() {
        LambdaQueryWrapper<LearningMaterialsMo> wrapper = new LambdaQueryWrapper<>();
        return ResultUtil.data(learningMaterialsService
            .list(wrapper.orderByDesc(LearningMaterialsMo::getUpdateTime).eq(LearningMaterialsMo::getStatusType, 1)));
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
