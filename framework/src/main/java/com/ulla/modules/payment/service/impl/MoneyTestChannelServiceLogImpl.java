package com.ulla.modules.payment.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.payment.entity.MoneyTestChannelLogEntity;
import com.ulla.modules.payment.mapper.MoneyTestChannelLogMapper;
import com.ulla.modules.payment.service.MoneyTestChannelLogService;

@Service
public class MoneyTestChannelServiceLogImpl extends ServiceImpl<MoneyTestChannelLogMapper, MoneyTestChannelLogEntity>
    implements MoneyTestChannelLogService {}
