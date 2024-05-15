package com.ulla.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.controller.UserBaseController;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.service.DepositConfigHistoryService;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 入金配置表 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@RestController
@RequestMapping("/depositConfig")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class DepositConfigHistoryController extends UserBaseController {
    final DepositConfigHistoryService depositConfigHistoryService;

    @GetMapping(value = "/getAllDepositConfig")
    public ResultMessageVo getAllDepositConfig() {
        return depositConfigHistoryService.getAllDepositConfig();
    }

    @GetMapping(value = "/getDepositBonus")
    public ResultMessageVo getDepositBonus(@RequestParam("amount") @Min(1) @Max(100000000) Integer amount,
        @RequestParam("uid") @Length(min = 1) String uid) {
        return depositConfigHistoryService.getDepositBonus(amount, getUserId(uid), System.currentTimeMillis());
    }
}
