package com.ulla.controller;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.utils.IdUtils;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.qo.UpdateBalanceQo;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.service.BalanceLogService;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.assets.vo.BalanceLogParameterVO;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.auth.mo.UserLevelMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.payment.mapper.MoneyPaymentTransactionMapper;

import io.swagger.annotations.ApiOperation;
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

    final Cache cache;

    final BalanceService balanceService;

    final BalanceLogService balanceLogService;

    final MoneyPaymentTransactionMapper moneyPaymentTransactionMapper;

    final UserMapper userMapper;

    /**
     * 后台财务的资金流水列表
     */
    @ApiOperation("资金流水表列表查询接口")
    @PostMapping(value = "/balanceLogListByParamer")
    public ResultMessageVo balanceLogListByParamer(@RequestBody BalanceLogParameterVO vo) {
        ResultMessageVo resultMessageVo = balanceLogService.balanceLogListByParamer(vo);
        if (ObjectUtils.isEmpty(resultMessageVo)) {
            return null;
        }
        return resultMessageVo;
    }

    /**
     * @Description 后台操作用户的资金流水
     * @author zhuyongdong
     * @since 2023-04-11 16:22:29
     */
    @ApiOperation("后台操作用户的资金流水")
    @PostMapping(value = "/updateBalance")
    public ResultMessageVo updateBalance(@Validated @RequestBody UpdateBalanceQo updateBalanceQo) {
        BalanceChangeVo balanceChangeVo = new BalanceChangeVo();
        switch (updateBalanceQo.getType()) {
            case 0:
                balanceChangeVo.setBonusAmount(updateBalanceQo.getAmount());
                break;
            case 1:
                balanceChangeVo.setAmount(updateBalanceQo.getAmount());
                UserMo userMo = userMapper.selectById(updateBalanceQo.getUserId());
                // 判断用户等级是否提升

                BigDecimal orderAmountCount = new BigDecimal(
                    moneyPaymentTransactionMapper.getOrderAmountCount(updateBalanceQo.getUserId()).toPlainString())
                        .add(updateBalanceQo.getAmount());
                Gson gson = new Gson();
                Type type = new TypeToken<List<UserLevelMo>>() {}.getType();
                List<UserLevelMo> list = gson.fromJson(cache.get("user:level:list").toString(), type);
                UserLevelMo userLevelMo = list.stream()
                    .filter(f -> ObjectUtils.isNotEmpty(f.getPromotionConditions())
                        && orderAmountCount.compareTo(new BigDecimal(f.getPromotionConditions())) >= 0)
                    .sorted(Comparator.comparing(co -> new BigDecimal(co.getPromotionConditions()),
                        Comparator.reverseOrder()))
                    .findFirst().orElse(null);

                // 等级有变化
                if (ObjectUtils.isNotEmpty(userLevelMo) && !userLevelMo.getLevel().equals(userMo.getUserLevel())) {
                    userMo.setUserLevel(userLevelMo.getLevel());
                    userMapper.updateById(userMo);
                    BigDecimal levelRewards = new BigDecimal(userLevelMo.getPromotionConditions())
                        .multiply(new BigDecimal(userLevelMo.getAssetIncome()).divide(new BigDecimal("100")));

                    balanceChangeVo.setBonusAmount(levelRewards);
                    if (ObjectUtils.isNotEmpty(updateBalanceQo.getRemark())) {
                        updateBalanceQo.setRemark(updateBalanceQo.getRemark() + "," + "会员升级奖励");
                    } else {
                        updateBalanceQo.setRemark("会员升级奖励");
                    }
                }
                break;
            case 3:
                balanceChangeVo.setVirtualAmount(updateBalanceQo.getAmount());
        }
        balanceChangeVo.setUid(updateBalanceQo.getUserId());
        balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.ADMIN);
        balanceChangeVo
            .setRemark(updateBalanceQo.getRemark() + "操作管理：" + UserUtil.getOpenId() + "操作IP：" + UserUtil.getIp());
        balanceChangeVo.setBusinessNo("#" + IdUtils.get8SimpleUUID());
        return balanceService.transactionChangeBalanceAndSaveLog(balanceChangeVo);
    }

}
