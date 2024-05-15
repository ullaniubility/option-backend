package com.ulla.modules.payment.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.payment.entity.MoneyTestChannelEntity;
import com.ulla.modules.payment.mapper.MoneyTestChannelMapper;
import com.ulla.modules.payment.service.MoneyTestChannelService;

@Service
public class MoneyTestChannelServiceImpl extends ServiceImpl<MoneyTestChannelMapper, MoneyTestChannelEntity>
    implements MoneyTestChannelService {}
