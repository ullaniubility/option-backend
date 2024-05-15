package com.ulla.modules.payment.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.payment.entity.MoneyPaymentChannelEntity;
import com.ulla.modules.payment.mapper.MoneyPaymentChannelMapper;
import com.ulla.modules.payment.service.MoneyPaymentChannelService;

@Service
public class MoneyPaymentChannelServiceImpl extends ServiceImpl<MoneyPaymentChannelMapper, MoneyPaymentChannelEntity>
    implements MoneyPaymentChannelService {

}