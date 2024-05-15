package com.ulla.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.SectionConfigMo;
import com.ulla.modules.business.qo.SectionConfigQo;
import com.ulla.modules.business.service.SectionConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "结算时间点配置")
@Slf4j
@RestController
@RequestMapping("/sectionConfig")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SectionConfigController {

    final SectionConfigService sectionConfigService;

    @ApiOperation(value = "获取结算时间点配置分页列表")
    @GetMapping("/getByPage")
    public ResultMessageVo<IPage<SectionConfigMo>> getByPage(SectionConfigQo qo) {
        return ResultUtil.data(sectionConfigService.queryByParams(qo));
    }

    @ApiOperation(value = "获取结算时间点配置列表")
    @GetMapping("/getList")
    public ResultMessageVo<List<SectionConfigMo>> getList(SectionConfigQo qo) {
        return ResultUtil.data(sectionConfigService.getList(qo));
    }

    /**
     * 新增结算时间点配置
     */
    @ApiOperation(value = "新增交结算时间点配置", notes = "新增交结算时间点配置")
    @PostMapping("/add")
    public ResultMessageVo add(@Validated @RequestBody SectionConfigMo mo) {
        try {
            return ResultUtil.data(sectionConfigService.save(mo));
        } catch (Exception e) {
            log.info("新增交结算时间点配置出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    /**
     * 获取结算时间点配置
     */
    @ApiOperation(value = "获取结算时间点配置", notes = "获取结算时间点配置")
    @GetMapping("/getById")
    public ResultMessageVo<SectionConfigMo> getById(@NotNull(message = "数据编号不能为空") Long id) {
        try {
            return ResultUtil.data(sectionConfigService.getById(id));
        } catch (Exception e) {
            log.info("获取结算时间点配置出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    /**
     * 编辑结算时间点配置
     */
    @ApiOperation(value = "编辑结算时间点配置", notes = "编辑结算时间点配置")
    @PostMapping("/update")
    public ResultMessageVo update(@Validated @RequestBody SectionConfigMo mo) {
        try {
            if (null == mo.getId()) {
                return ResultUtil.error(4002, "The data number cannot be empty");
            }
            return ResultUtil.data(sectionConfigService.updateById(mo));
        } catch (Exception e) {
            log.info("编辑结算时间点配置出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

}
