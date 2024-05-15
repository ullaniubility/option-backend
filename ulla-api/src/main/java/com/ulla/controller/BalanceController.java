package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.service.BalanceService;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 用户资产 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class BalanceController {

    final BalanceService balanceService;

    @GetMapping(value = "/getUserBalance")
    public ResultMessageVo getUserBalance() {
        return balanceService.getUserBalance(UserUtil.getUid());
    }
}
