package com.ulla.modules.payment.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.payment.entity.PaymentCurrencyEntity;
import com.ulla.modules.payment.mapper.PaymentCurrencyMapper;
import com.ulla.modules.payment.service.PaymentCurrencyService;

@Service("paymentCurrencyService")
public class PaymentCurrencyServiceImpl extends ServiceImpl<PaymentCurrencyMapper, PaymentCurrencyEntity>
    implements PaymentCurrencyService {

}