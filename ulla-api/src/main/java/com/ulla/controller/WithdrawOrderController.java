package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ulla.api.BinanceApi;
import com.ulla.common.controller.UserBaseController;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.service.WithdrawOrderService;
import com.ulla.modules.assets.vo.WithdrawOrderVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-11
 */
@RestController
@RequestMapping("/withdrawOrder")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
@Slf4j
public class WithdrawOrderController extends UserBaseController {

    final WithdrawOrderService withdrawOrderService;

    final BinanceApi binanceApi;

    @PostMapping(value = "/createOrder")
    public ResultMessageVo createOrder(@RequestBody WithdrawOrderVo withdrawOrderVo) {
        withdrawOrderVo.setUserId(getUserId(withdrawOrderVo.getUid()));
        return ResultUtil.data(withdrawOrderService.createOrder(withdrawOrderVo));
    }

    @GetMapping(value = "/test")
    public void binanceTest() {

        Response<Object> ticker24Hr = binanceApi.ticker24Hr("[\"BTCUSDT\",\"BNBUSDT\"]", "MINI");
        System.out.println(ticker24Hr);

    }
}
