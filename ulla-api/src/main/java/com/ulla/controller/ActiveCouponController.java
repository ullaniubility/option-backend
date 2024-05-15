package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.service.ActiveCouponService;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 促销优惠券表 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@RestController
@RequestMapping("/activeCoupon")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class ActiveCouponController {

    final ActiveCouponService couponService;

    @GetMapping(value = "/checkCoupon")
    public ResultMessageVo checkCoupon(@RequestParam("couponCode") String couponCode,
        @RequestParam("amount") Integer amount) {
        return couponService.checkCoupon(couponCode, amount, UserUtil.getUid());
    }

}
