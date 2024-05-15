package com.ulla.modules.assets.service.impl;

import static com.ulla.constant.NumberConstant.*;
import static com.ulla.modules.assets.constants.ActiveConstant.PERCENTAGE_MODEL;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.BusinessNoUtil;
import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.common.vo.exception.ServiceException;
import com.ulla.modules.assets.mapper.ActiveCouponMapper;
import com.ulla.modules.assets.mo.ActiveCouponMo;
import com.ulla.modules.assets.mo.ActiveMo;
import com.ulla.modules.assets.service.ActiveCouponService;
import com.ulla.modules.assets.service.ActiveService;
import com.ulla.modules.assets.vo.ActiveVo;
import com.ulla.modules.assets.vo.CouponVo;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;

import cn.hutool.core.util.ObjectUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 促销优惠券表 服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Service
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ActiveCouponServiceImpl extends ServiceImpl<ActiveCouponMapper, ActiveCouponMo>
    implements ActiveCouponService {

    @Lazy
    @Resource
    ActiveService activeService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void createCoupon(ActiveVo activeVo) {
        log.error("异步线程名称：{}", Thread.currentThread().getName());
        Integer num = activeVo.getNum();
        Set<String> set = new HashSet<>(num);
        while (set.size() < num) {
            set.add(BusinessNoUtil.generateShortUuid());
        }
        List<ActiveCouponMo> activeCouponMos = set.stream()
            .map(s -> ActiveCouponMo.builder().activeId(activeVo.getActiveId()).couponCode(s).useFlag(ZERO).build())
            .collect(Collectors.toList());
        saveBatch(activeCouponMos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized ResultMessageVo checkCoupon(String couponCode, Integer amount, Long userId) {
        if (ObjectUtil.isEmpty(userId)) {
            return ResultUtil.error(ResultCodeEnums.USER_NOT_LOGIN);
        }
        CouponVo coupon = baseMapper.getByCodeAndAmount(couponCode, amount);
        if (ObjectUtil.isEmpty(coupon)) {
            return ResultUtil.error(ResultCodeEnums.COUPON_DO_NOT_MATE);
        }
        if (BooleanUtils.toBoolean(coupon.getUseFlag()) || ObjectUtil.isNotEmpty(coupon.getUserId())) {
            return ResultUtil.error(ResultCodeEnums.COUPON_EXPIRED);
        }
        if (!BooleanUtils.toBoolean(coupon.getState())) {
            return ResultUtil.error(ResultCodeEnums.COUPON_ACTIVITY_NOT_EXIST);
        }
        long now = System.currentTimeMillis();
        if (coupon.getBeginTime() > now) {
            return ResultUtil.error(ResultCodeEnums.COUPON_NOT_STARTED);
        }
        if (coupon.getEndTime() < now) {
            return ResultUtil.error(ResultCodeEnums.COUPON_HAS_ENDED);
        }
        Integer useNum = coupon.getUseNum() == null ? ONE : coupon.getUseNum();
        // 获取用户的使用总数
        long count = count(new LambdaQueryWrapper<ActiveCouponMo>().eq(ActiveCouponMo::getUserId, userId)
            .eq(ActiveCouponMo::getUseFlag, ONE).eq(ActiveCouponMo::getActiveId, coupon.getActiveId()));
        if (useNum <= count) {
            return ResultUtil.error(ResultCodeEnums.COUPON_LIMIT_ERROR);
        }
        // 获取用户的当天使用的总数
        Integer dayUseNum = coupon.getDayUseNum() == null ? ONE : coupon.getDayUseNum();
        Long dayOfStart = DateUtil.getDayOfStart();
        Long dayOfEnd = dayOfStart + T24T * T3600D * T1000L - ONE;
        long todayCount = count(new LambdaQueryWrapper<ActiveCouponMo>().eq(ActiveCouponMo::getUserId, userId)
            .eq(ActiveCouponMo::getUseFlag, ONE).eq(ActiveCouponMo::getActiveId, coupon.getActiveId())
            .between(ActiveCouponMo::getUseTime, dayOfStart, dayOfEnd));
        if (dayUseNum <= todayCount) {
            return ResultUtil.error(ResultCodeEnums.COUPON_ACTIVITY_TODAY_MAX_NUM);
        }
        Integer rewardModel = coupon.getRewardModel();
        Integer executeModel = coupon.getExecuteModel();
        Integer rewardAmount = coupon.getRewardAmount();
        // 绑定用户
        // ActiveCouponMo couponMo = new ActiveCouponMo();
        // couponMo.setId(coupon.getId());
        // couponMo.setUserId(userId);
        // updateById(couponMo);
        // 返回优惠券减免数据
        Map<String, Object> map = new HashMap<>(TWO);
        map.put("couponId", coupon.getId());
        map.put("couponDeductAmount", calculateDeductAmount(rewardModel, executeModel, rewardAmount, amount)
            .stripTrailingZeros().toPlainString());
        return ResultUtil.data(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean couponBindOrder(MoneyPaymentTransactionEntity entity) {
        // 优惠券id
        Long couponId = entity.getCouponId();
        // 用户id
        Long uid = entity.getUid();
        // 订单编号
        String orderNo = entity.getOrderId();
        // 订单入金金额
        Integer amount = Integer.parseInt(entity.getEstimatedDepositAmount().toString());
        // 优惠券减免金额
        BigDecimal preferentialAmount = entity.getPreferentialAmount();

        ActiveCouponMo activeCouponMo = getById(couponId);
        if (ObjectUtil.isEmpty(activeCouponMo)) {
            throw new ServiceException("促销券不存在");
        }
        if (ObjectUtil.isNotEmpty(activeCouponMo.getUserId())) {
            throw new ServiceException("促销券已被使用");
        }
        ActiveMo activeMo = activeService.getById(activeCouponMo.getActiveId());
        if (ObjectUtil.isEmpty(activeMo)) {
            throw new ServiceException("促销活动不存在");
        }
        if (System.currentTimeMillis() > activeMo.getEndTime()) {
            throw new ServiceException("促销活动已过期");
        }
        int rewardModel = activeMo.getRewardModel();
        switch (rewardModel) {
            // 无奖励
            case 0:
                break;
            // 固定奖励
            case 1:
                if (ObjectUtils.isNotEmpty(entity.getRewardAmount())) {
                    entity.setRewardAmount(entity.getRewardAmount().add(new BigDecimal(activeMo.getRewardAmount())));
                } else {
                    entity.setRewardAmount(new BigDecimal(activeMo.getRewardAmount()));
                }

                break;
            // 百分比奖励
            case 2:
                BigDecimal rewardAmount = calculateDeductAmount(activeMo.getRewardModel(), activeMo.getExecuteModel(),
                    activeMo.getRewardAmount(), amount);
                if (activeMo.getExecuteModel().intValue() == 1) {
                    if (ObjectUtils.isNotEmpty(entity.getRewardAmount())) {
                        entity.setRewardAmount(entity.getRewardAmount().add(rewardAmount));
                    } else {
                        entity.setRewardAmount(rewardAmount);
                    }
                } else {
                    if (preferentialAmount.compareTo(rewardAmount) != 0) {
                        throw new ServiceException("促销券减免金额被篡改");
                    }
                    // 入金金额 - 页面选择的入金金额
                    BigDecimal estimatedDepositAmount = new BigDecimal(entity.getEstimatedDepositAmount().toString());
                    // 到账币种数量 = 入金金额(页面选择的入金金额) - 促销优惠金额
                    BigDecimal currencyAmount = estimatedDepositAmount.subtract(entity.getPreferentialAmount());
                    if (entity.getCurrencyAmount().compareTo(currencyAmount) != 0) {
                        throw new ServiceException(ResultCodeEnums.REWARD_CONFIG_NOT_EXIST);
                    }
                }
                break;
        }
        entity.setRemark(activeMo.getName());
        ActiveCouponMo coupon = new ActiveCouponMo();
        coupon.setId(couponId);
        coupon.setUserId(uid);
        coupon.setUseFlag(ONE);
        coupon.setDepositOrderNo(orderNo);
        coupon.setUseTime(DateUtil.getDate13line());
        return updateById(coupon);
    }

    @Override
    public Boolean couponUnbindOrder(@NonNull Long couponId) {
        return baseMapper.couponUnbindOrder(couponId) > 0;
    }

    private BigDecimal calculateDeductAmount(Integer rewardModel, Integer executeModel, Integer rewardAmount,
        Integer amount) {
        if (PERCENTAGE_MODEL.equals(rewardModel)) {
            if (executeModel.intValue() == 2) {
                return BigDecimal.valueOf(rewardAmount)
                    .divide(BigDecimal.TEN.multiply(BigDecimal.TEN), TWO, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(amount)).setScale(TWO, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(amount).multiply(
                    (BigDecimal.valueOf(rewardAmount).divide(new BigDecimal("100"), TWO, BigDecimal.ROUND_DOWN)));
            }
        }
        return BigDecimal.valueOf(rewardAmount);
    }
}
