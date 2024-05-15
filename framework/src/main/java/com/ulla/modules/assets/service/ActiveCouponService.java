package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.ActiveCouponMo;
import com.ulla.modules.assets.vo.ActiveVo;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;

/**
 * <p>
 * 促销优惠券表 服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
public interface ActiveCouponService extends IService<ActiveCouponMo> {

    /**
     * 生成优惠券
     * 
     * @param activeVo
     */
    void createCoupon(ActiveVo activeVo);

    /**
     * 校验优惠券
     *
     * @param couponCode
     * @param amount
     * @param userId
     * @return
     */
    ResultMessageVo checkCoupon(String couponCode, Integer amount, Long userId);

    /**
     * 优惠券绑定订单号和用户
     * 
     * @param entity
     *            订单对象
     * @return
     */
    Boolean couponBindOrder(MoneyPaymentTransactionEntity entity);

    /**
     * 优惠券解绑（订单失败或订单失效时调用）
     * 
     * @param couponId
     *            优惠券id
     * @return
     */
    Boolean couponUnbindOrder(Long couponId);
}
