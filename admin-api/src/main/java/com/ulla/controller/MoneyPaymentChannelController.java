package com.ulla.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.PaymentConstant;
import com.ulla.modules.payment.entity.JsonFileEntity;
import com.ulla.modules.payment.entity.MoneyPaymentChannelEntity;
import com.ulla.modules.payment.entity.MoneyTestChannelEntity;
import com.ulla.modules.payment.entity.MoneyTestChannelLogEntity;
import com.ulla.modules.payment.service.MoneyPaymentChannelService;
import com.ulla.modules.payment.service.MoneyTestChannelLogService;
import com.ulla.modules.payment.service.MoneyTestChannelService;
import com.ulla.mybatis.util.PageUtil;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "公司收款链配置")
@Slf4j
@RestController
@RequestMapping("/payChannelConfig")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoneyPaymentChannelController {

    final MoneyPaymentChannelService channelService;

    final MoneyTestChannelService moneyTestChannelService;

    final MoneyTestChannelLogService moneyTestChannelLogService;

    @ApiOperation(value = "获取公司收款链列表")
    @GetMapping("/getList")
    public ResultMessageVo<List<MoneyPaymentChannelEntity>> getList() {
        return ResultUtil.data(channelService.list());
    }

    @ApiOperation(value = "分页获取公司收款链列表")
    @GetMapping("/listByPage")
    public ResultMessageVo<Page<MoneyPaymentChannelEntity>> listByPage(PageVo pageVo) {
        return ResultUtil.data(channelService.page(PageUtil.initPage(pageVo), null));
    }

    @ApiOperation(value = "新增公司收款链", notes = "新增公司收款链")
    @PostMapping("/add")
    public ResultMessageVo add(@Validated @RequestBody MoneyPaymentChannelEntity mo) {
        try {
            mo.setCreateBy(UserUtil.getUid());
            return ResultUtil.data(channelService.save(mo));
        } catch (Exception e) {
            log.info("新增公司收款链出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    @ApiOperation(value = "根据编号获取公司收款链详情", notes = "根据编号获取公司收款链详情")
    @GetMapping("/getById")
    public ResultMessageVo<MoneyPaymentChannelEntity> getById(@NotNull(message = "数据编号不能为空") Long id) {
        try {
            return ResultUtil.data(channelService.getById(id));
        } catch (Exception e) {
            log.info("根据编号获取公司收款链详情出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    @ApiOperation(value = "根据编号编辑公司收款链", notes = "根据编号编辑公司收款链")
    @PostMapping("/update")
    public ResultMessageVo update(@Validated @RequestBody MoneyPaymentChannelEntity mo) {
        try {
            if (null == mo.getId()) {
                return ResultUtil.error(4002, "The data number cannot be empty");
            }
            return ResultUtil.data(channelService.updateById(mo));
        } catch (Exception e) {
            log.info("根据编号编辑公司收款链出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    @ApiOperation(value = "根据编号删除公司收款链详情", notes = "根据编号删除公司收款链详情")
    @GetMapping("/deleteById")
    public ResultMessageVo deleteById(@NotNull(message = "数据编号不能为空") Long id) {
        try {
            return ResultUtil.data(channelService.removeById(id));
        } catch (Exception e) {
            log.info("根据编号删除公司收款链详情出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    @ApiOperation(value = "导入公司收款链列表")
    @PostMapping("/uploadFile")
    public ResultMessageVo uploadFile(@RequestParam(value = "file") MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        String jsonString = new String(fileBytes, StandardCharsets.UTF_8.name());
        List<JsonFileEntity> list = JSON.parseArray(jsonString, JsonFileEntity.class);
        // CompletableFuture.runAsync(() -> {
        list.stream().forEach(item -> {
            if (item.getNet().equals("ETH")) {
                MoneyPaymentChannelEntity entity = new MoneyPaymentChannelEntity();
                BeanUtils.copyProperties(item, entity);
                entity.setName("USDT(Tether) ERC20");
                entity.setContractAddress(PaymentConstant.ERC20_USDT_CONTRACT);
                entity.setSymbol("USDT");
                entity
                    .setIconUrl("https://ullafile.oss-cn-chengdu.aliyuncs.com/5/401d89c33d924439b3c19e91ec24d49c.png");
                entity.setCreateBy(0l);
                channelService.save(entity);
            } else if (item.getNet().equals("TRX")) {
                MoneyPaymentChannelEntity entity = new MoneyPaymentChannelEntity();
                BeanUtils.copyProperties(item, entity);
                entity.setName("USDT(Tron) TRC20");
                entity.setContractAddress(PaymentConstant.TRC20_USDT_CONTRACT);
                entity.setSymbol("USDT");
                entity.setIconUrl(
                    "https://ullafile.oss-cn-chengdu.aliyuncs.com/5%2C5/4a6e3468ad0c47ce9be377b6064f2ded.png");
                entity.setCreateBy(0l);
                channelService.save(entity);
            }
        });
        // }).join();
        return ResultUtil.success();
    }

    /**
     * 测试用---只能编辑--启用禁用----编辑人要记录--每次编辑的记录数据新增到log表
     */
    @ApiOperation("編輯测试链地址")
    @PostMapping(value = "/updateTest")
    public ResultMessageVo updateTest(@RequestBody MoneyTestChannelEntity money) {
        UpdateWrapper<MoneyTestChannelEntity> updateWrapper = new UpdateWrapper<>();
        if (ObjectUtil.isNotEmpty(money.getName())) {
            updateWrapper.set("name", money.getName());
        }
        if (ObjectUtil.isNotEmpty(money.getNet())) {
            updateWrapper.set("net", money.getNet());
        }
        if (ObjectUtil.isNotEmpty(money.getSymbol())) {
            updateWrapper.set("symbol", money.getSymbol());
        }
        if (ObjectUtil.isNotEmpty(money.getAddress())) {
            updateWrapper.set("adress", money.getAddress());
        }
        if (ObjectUtil.isNotEmpty(money.getContractAddress())) {
            updateWrapper.set("contract_address", money.getContractAddress());
        }
        if (ObjectUtil.isNotEmpty(money.getIconUrl())) {
            updateWrapper.set("icon_url", money.getIconUrl());
        }
        if (ObjectUtil.isNotEmpty(money.getOffFlag())) {
            updateWrapper.set("off_flag", money.getOffFlag());
        }
        updateWrapper.set("create_by", UserUtil.getUid());
        updateWrapper.set("update_time", System.currentTimeMillis());
        updateWrapper.eq("id", money.getId());
        moneyTestChannelService.update(updateWrapper);
        // 将数据copy一份到日志表里去
        MoneyTestChannelLogEntity moneyTestChannelLogEntity = new MoneyTestChannelLogEntity();
        BeanUtils.copyProperties(updateWrapper, moneyTestChannelLogEntity);
        moneyTestChannelLogEntity.setCreateTime(System.currentTimeMillis());
        moneyTestChannelLogService.save(moneyTestChannelLogEntity);
        return ResultUtil.success(200, "保存成功");
    }

}
