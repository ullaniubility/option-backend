package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.enums.BalanceTypeEnums;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.business.qo.UserWithdrawalQo;
import com.ulla.modules.business.service.MoneyDepositService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @Description 出金
 * @author zhuyongdong
 * @since 2023-04-09 10:54:28
 */
@Api(tags = "出金")
@RestController
@RequestMapping("/withdrawal")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WithdrawalController {

    final MoneyDepositService moneyDepositService;

    final BalanceService balanceService;

    /**
     * 用户提现
     */
    @PostMapping("/userWithdrawal")
    @ApiOperation(value = "用户提现", notes = "用户提现")
    public ResultMessageVo userWithdrawal(@RequestBody UserWithdrawalQo userWithdrawalQo) {
        return moneyDepositService.userWithdrawal(userWithdrawalQo, UserUtil.getUid(), UserUtil.getOpenId());
    }

    /**
     * 用户提现额度查询
     */
    @GetMapping("/withdrawalCount")
    @ApiOperation(value = "用户提现额度查询", notes = "用户提现额度查询")
    public ResultMessageVo withdrawalCount() {
        return moneyDepositService.withdrawalCount(UserUtil.getUid());
    }

    /**
     * 用户真实金额查询
     */
    @GetMapping("/userBalance")
    @ApiOperation(value = "用户", notes = "用户真实金额查询")
    public ResultMessageVo userBalance() {
        return ResultUtil.data(
            balanceService.selectByUserId(UserUtil.getUid(), BalanceTypeEnums.REAL_BALANCE.getType()).getBalance());
    }

}
