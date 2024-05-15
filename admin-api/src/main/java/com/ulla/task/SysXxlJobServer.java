package com.ulla.task;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ulla.cache.Cache;
import com.ulla.common.utils.DateUtil;
import com.ulla.modules.auth.mapper.UserLevelMapper;
import com.ulla.modules.auth.mo.UserLevelMo;
import com.ulla.modules.binance.mo.ExchangeRateMo;
import com.ulla.modules.binance.service.QuotationService;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mapper.TransactionConfigMapper;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.mo.TransactionConfigMo;
import com.xxl.job.core.handler.annotation.XxlJob;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 行情干预算法
 * @since 2023/2/23 11:41
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysXxlJobServer {

    final Cache cache;

    final TransactionConfigMapper transactionConfigMapper;

    final QuotationService quotationService;

    final OrderMapper orderMapper;

    final UserLevelMapper userLevelMapper;

    /**
     * 整点刷新平仓缓存
     */
    @XxlJob("refreshClosePosition")
    public void refreshClosePosition() {
        try {
            TransactionConfigMo transactionConfigMo = transactionConfigMapper.selectById(1);
            String max = new BigDecimal(transactionConfigMo.getLossRatio().toString())
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
            String min = new BigDecimal(transactionConfigMo.getWithdrawal().toString())
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
            Map<String, String> map = new HashMap<>();
            map.put("closePositionMax", max);
            map.put("closePositionMin", min);
            cache.put("user:closePosition:map", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每5分钟刷新汇率
     */
    @XxlJob("refreshExchangeRate")
    public void refreshExchangeRate() {
        try {
            List<ExchangeRateMo> list = quotationService.getExchangeRate();
            list.stream().filter(k -> BooleanUtils.toBoolean(k.getStatusFlag())).collect(Collectors.toList())
                .forEach(item -> {
                    quotationService.exchangeRate(item.getChildName());
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每6分钟清理订单
     */
    @XxlJob("deleteOrder")
    public void deleteOrder() {
        try {
            Long dateTime = DateUtil.getReviseDate13line(null) - 1000 * 60 * 10;
            LambdaQueryWrapper<OrderMo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.le(OrderMo::getOrderTime, dateTime);
            queryWrapper.and(
                wrapper -> wrapper.eq(OrderMo::getStatus, BigDecimal.ZERO).or().eq(OrderMo::getStatus, BigDecimal.ONE));
            orderMapper.selectList(queryWrapper).stream().forEach(item -> {
                orderMapper.deleteById(item);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("每天凌晨整点刷新会员等级缓存")
    @XxlJob("refreshUserLevel")
    public void refreshUserLevel() {
        try {
            LambdaQueryWrapper<UserLevelMo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserLevelMo::getOffFlag, 0);
            wrapper.orderByAsc(UserLevelMo::getSortNum);
            List<UserLevelMo> list = userLevelMapper.selectList(wrapper);
            cache.put("user:level:list", list);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
