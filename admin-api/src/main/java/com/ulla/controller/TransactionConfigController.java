package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mapper.TransactionConfigMapper;
import com.ulla.modules.business.mo.TransactionConfigMo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "交易配置")
@RestController
@RequestMapping("/transactionConfig")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionConfigController {

    final TransactionConfigMapper transactionConfigMapper;

    /**
     * 编辑交易配置
     */
    @PostMapping("/update/{id}")
    @ApiOperation(value = "编辑交易配置", notes = "编辑交易配置")
    public ResultMessageVo update(@PathVariable Long id, @RequestBody TransactionConfigMo transactionConfigMo) {
        if (transactionConfigMo == null || id == null || !id.equals(transactionConfigMo.getId())) {
            return ResultUtil.error(4002, "Parameter error");
        }
        if (ObjectUtils.isNotEmpty(transactionConfigMo.getLossRatio())) {
            transactionConfigMo.setLossRatio(transactionConfigMo.getLossRatio());
        }
        if (ObjectUtils.isNotEmpty(transactionConfigMo.getWithdrawal())) {
            transactionConfigMo.setWithdrawal(transactionConfigMo.getWithdrawal());
        }
        if (ObjectUtils.isNotEmpty(transactionConfigMo.getDeleteFlag())) {
            transactionConfigMo.setDeleteFlag(transactionConfigMo.getDeleteFlag());
        }
        transactionConfigMo.setUpdateTime(System.currentTimeMillis());
        return ResultUtil.data(this.transactionConfigMapper.updateById(transactionConfigMo));
    }

    @ApiOperation(value = "交易配置", notes = "交易配置列表")
    @GetMapping("/list")
    public ResultMessageVo LearningMaterialsList() {
        return ResultUtil.data(this.transactionConfigMapper.getList());
    }

}
