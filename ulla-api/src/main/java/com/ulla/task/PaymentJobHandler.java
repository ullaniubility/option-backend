package com.ulla.task;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ulla.cache.Cache;
import com.ulla.common.utils.DateUtil;
import com.ulla.constant.PaymentConstant;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.mapper.MoneyPaymentTransactionMapper;
import com.ulla.modules.payment.service.MoneyPaymentTransactionService;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author michael
 * @Description 入金相关功能定时任务
 * @since 2023/03/02 11:41
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentJobHandler {

    final Cache cache;

    final MoneyPaymentTransactionMapper moneyPaymentTransactionMapper;

    final MoneyPaymentTransactionService moneyPaymentTransactionService;

    /**
     * 处理失效订单 - 订单时长超过30分钟切状态仍在待付款状态订单处理为失效订单
     */
    @XxlJob("processInvalidOrders")
    public void processInvalidOrders() {
        Long endTime = DateUtil.getReviseDate13line(null) - 1000 * 60 * 30;
        LambdaQueryWrapper<MoneyPaymentTransactionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MoneyPaymentTransactionEntity::getOrderStatus, 0);
        wrapper.lt(MoneyPaymentTransactionEntity::getCreateTime, endTime);
        List<MoneyPaymentTransactionEntity> list = moneyPaymentTransactionMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(item -> {
                item.setOrderStatus(PaymentConstant.lose_efficacy);
                item.setUpdateTime(new Date().getTime());
            });
            moneyPaymentTransactionService.updateBatchById(list);
        }
    }

}
