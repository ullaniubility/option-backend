package com.ulla.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.payment.entity.MoneyPaymentChannelEntity;
import com.ulla.modules.payment.entity.MoneyTestChannelEntity;
import com.ulla.modules.payment.mapper.MoneyTestChannelMapper;
import com.ulla.modules.payment.service.MoneyPaymentChannelService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "公司收款链配置")
@Slf4j
@RestController
@RequestMapping("/payChannel")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoneyPaymentChannelController {

    final MoneyPaymentChannelService channelService;

    final MoneyTestChannelMapper testChannelMapper;

    @ApiOperation(value = "获取公司收款链列表")
    @GetMapping("/getList")
    public ResultMessageVo<List<MoneyPaymentChannelEntity>> getList(String net) {
        MoneyTestChannelEntity testChannel = testChannelMapper.selectById(1l);
        if (null != testChannel && testChannel.getOffFlag().intValue() == 1) {
            List<MoneyPaymentChannelEntity> list = Lists.newArrayList();
            MoneyPaymentChannelEntity paymentChannel = new MoneyPaymentChannelEntity();
            BeanUtils.copyProperties(testChannel, paymentChannel);
            list.add(paymentChannel);
            return ResultUtil.data(list);
        }
        if (StringUtils.isBlank(net)) {
            return ResultUtil.error(ResultCodeEnums.PARAMS_ERROR);
        }
        LambdaQueryWrapper<MoneyPaymentChannelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MoneyPaymentChannelEntity::getNet, net);
        List<MoneyPaymentChannelEntity> list = channelService.list(wrapper);
        List<MoneyPaymentChannelEntity> newList = Lists.newArrayList();
        Random random = new Random();
        newList.add(list.get(random.nextInt(list.size())));
        return ResultUtil.data(newList);
    }

}
