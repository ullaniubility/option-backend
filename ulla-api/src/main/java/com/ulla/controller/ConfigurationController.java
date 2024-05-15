package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.service.IConfigurationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "设置配置")
@RestController
@RequestMapping("/configuration")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationController {
    final IConfigurationService configurationService;

    /**
     * 客户端不分页
     */
    @ApiOperation(value = "配置列表", notes = "配置列表")
    @GetMapping("/list")
    public ResultMessageVo ConfigurationList() {
        return ResultUtil.data(configurationService.list());
    }
}
