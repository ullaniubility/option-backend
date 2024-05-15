package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ulla.common.utils.IdUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.service.SysConfigService;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.service.impl.BalanceServiceImpl;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.business.mo.MoneyDepositMo;
import com.ulla.modules.business.qo.MoneyDepositQo;
import com.ulla.modules.business.service.MoneyDepositService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "出金订单查询")
@RestController
@RequestMapping("/moneyDeposit")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoneyDepositController {

    final MoneyDepositService moneyDepositService;

    final SysConfigService sysConfigService;

    final BalanceServiceImpl balanceService;

    /**
     * 出金订单
     */
    @ApiOperation("出金订单列表查询")
    @PostMapping(value = "/expenditureList")
    public ResultMessageVo expenditureList(@RequestBody MoneyDepositQo qo) {
        return moneyDepositService.expenditureList(qo);
    }

    /**
     * 出金订单审核----暂时是改状态
     */
    @GetMapping("/updateStatus")
    @ApiOperation(value = "出金订单审核", notes = "出金订单审核")
    public ResultMessageVo updateStatus(Long id, Integer orderStatus, String remark) {
        MoneyDepositMo one =
            this.moneyDepositService.getOne(Wrappers.<MoneyDepositMo>query().lambda().eq(MoneyDepositMo::getId, id));
        if (one.getOrderStatus() != 0) {
            return ResultUtil.error(13001, "Please do not repeat the review for those that have already been reviewed");
        }
        UpdateWrapper<MoneyDepositMo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("order_status", orderStatus);
        if (orderStatus == 4) {
            updateWrapper.set("remark", remark);
            BalanceChangeVo balanceChangeVo = new BalanceChangeVo();
            balanceChangeVo.setAmount(one.getDepositMonetaryAmount());
            balanceChangeVo.setUid(one.getUid());
            balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.ADMIN);
            balanceChangeVo.setBusinessNo("#" + IdUtils.get8SimpleUUID());
            balanceService.transactionChangeBalanceAndSaveLog(balanceChangeVo);
        } else {
            updateWrapper.set("remark", null);
        }
        updateWrapper.eq("id", id);
        return ResultUtil.data(moneyDepositService.update(updateWrapper));
    }

}
