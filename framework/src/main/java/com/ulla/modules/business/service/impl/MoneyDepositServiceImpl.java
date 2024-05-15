package com.ulla.modules.business.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.DateUtil;
import com.ulla.common.utils.IdUtils;
import com.ulla.common.utils.PageUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.SysConfigMo;
import com.ulla.modules.admin.service.SysConfigService;
import com.ulla.modules.assets.enums.BalanceTypeEnums;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.mo.BalanceMo;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.auth.mapper.UserAuthorityMapper;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.auth.mo.UserAuthorityMo;
import com.ulla.modules.business.mapper.MoneyDepositMapper;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mo.MoneyDepositMo;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.mo.WithdrawalMo;
import com.ulla.modules.business.qo.MoneyDepositQo;
import com.ulla.modules.business.qo.UserWithdrawalQo;
import com.ulla.modules.business.service.MoneyDepositService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoneyDepositServiceImpl extends ServiceImpl<MoneyDepositMapper, MoneyDepositMo>
    implements MoneyDepositService {

    final MoneyDepositMapper moneyDepositMapper;

    final UserMapper userMapper;

    final BalanceService balanceService;

    final OrderMapper orderMapper;

    final SysConfigService sysConfigService;

    final UserAuthorityMapper userAuthorityMapper;

    @Override
    public ResultMessageVo expenditureList(MoneyDepositQo qo) {
        Integer page = (qo.getPage() - 1) * qo.getPageSize();
        qo.setPage(page);
        List<MoneyDepositMo> list = moneyDepositMapper.getExpenditureList(qo);
        Integer listCount = moneyDepositMapper.getExpenditureListCount(qo);
        PageUtils pageUtils = new PageUtils(list, listCount, qo.getPageSize(), page);
        return ResultUtil.data(pageUtils);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo userWithdrawal(UserWithdrawalQo userWithdrawalQo, Long uid, String openId) {
        LambdaQueryWrapper<UserAuthorityMo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuthorityMo::getAlloweWithdrawal, 0);
        queryWrapper.eq(UserAuthorityMo::getUid, uid);
        MoneyDepositMo mo = new MoneyDepositMo();
        BeanUtils.copyProperties(userWithdrawalQo, mo);
        Long beforeTime = DateUtil.getDate13line();
        String orderNo = IdUtils.get12SimpleUUID();
        BigDecimal depositMonetaryAmount = mo.getDepositMonetaryAmount();
        if (depositMonetaryAmount.compareTo(new BigDecimal("0.0")) <= 0) {
            return ResultUtil.error(ResultCodeEnums.WALLET_APPLY_ERROR);
        }

        Long flag = userAuthorityMapper.selectCount(queryWrapper);
        if (ObjectUtils.isNotEmpty(flag) && flag.intValue() > 0) {
            return ResultUtil.error(ResultCodeEnums.WALLET_WITHDRAWAL_AUTHORITY_ERROR);
        }

        Gson gson = new Gson();
        SysConfigMo sysConfigMo = sysConfigService.getSysConfigMoByKey("WITHDRAWAL");
        WithdrawalMo withdrawalMo = gson.fromJson(sysConfigMo.getConfigValue(), WithdrawalMo.class);

        BalanceMo balanceMo = balanceService.selectByUserId(uid, BalanceTypeEnums.REAL_BALANCE.getType());
        if (balanceMo == null || balanceMo.getBalance().compareTo(depositMonetaryAmount) < 0) {
            return ResultUtil.error(ResultCodeEnums.WALLET_WITHDRAWAL_INSUFFICIENT);
        }

        if (sysConfigMo.getConfigType().equals("Y")) {
            if (depositMonetaryAmount.compareTo(new BigDecimal(withdrawalMo.getLowAmount())) < 0) {
                return ResultUtil.error(ResultCodeEnums.WALLET_WITHDRAWAL_LOW);
            }
            LambdaQueryWrapper<OrderMo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderMo::getUid, uid);
            wrapper.gt(OrderMo::getWithdrawalAmount, 0);
            wrapper.eq(OrderMo::getStatus, 2);
            wrapper.groupBy(OrderMo::getId);
            wrapper.orderByAsc(OrderMo::getId);
            List<OrderMo> listOrders = orderMapper.selectList(wrapper);
            BigDecimal withdrawalAmount =
                listOrders.stream().map(OrderMo::getWithdrawalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            withdrawalAmount = withdrawalAmount.multiply(new BigDecimal(withdrawalMo.getWithdrawalAmountPercent())
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
            if (depositMonetaryAmount.compareTo(withdrawalAmount) > 0) {
                return ResultUtil.error(ResultCodeEnums.WALLET_WITHDRAWAL_HEIGHT);
            }
            BigDecimal tempAmount = depositMonetaryAmount.multiply(new BigDecimal("2"));
            withdrawalAmount = new BigDecimal("0.0");
            List<OrderMo> orderIds = Lists.newArrayList();
            for (OrderMo orderMo : listOrders) {
                withdrawalAmount = withdrawalAmount.add(orderMo.getWithdrawalAmount());
                if (withdrawalAmount.compareTo(tempAmount) > 0) {
                    orderMo.setWithdrawalAmount(withdrawalAmount.subtract(tempAmount));
                    orderIds.add(orderMo);
                    break;
                }
                orderMo.setWithdrawalAmount(new BigDecimal(0.0));
                orderIds.add(orderMo);
            }
            orderIds.stream().forEach(orderMo -> {
                orderMapper.updateById(orderMo);
            });
        }

        // 余额处理
        BalanceChangeVo balanceChangeVo = new BalanceChangeVo();
        balanceChangeVo.setAmount(BigDecimal.ZERO.subtract(depositMonetaryAmount));
        balanceChangeVo.setUid(uid);
        balanceChangeVo.setBusinessNo(orderNo);
        balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.WITHDRAW);
        balanceService.changeBalanceAndSaveLog(balanceChangeVo);

        mo.setUid(uid);
        mo.setOpenId(openId);
        mo.setAddressLower(mo.getAddress().toLowerCase());
        mo.setOrderNo(orderNo);
        mo.setOrderStatus(0);
        mo.setCreateBy(uid);
        mo.setCreateTime(beforeTime);
        mo.setLogoUrl("no file");
        baseMapper.insert(mo);
        return ResultUtil.success(200, "提现提交申请成功!");
    }

    @Override
    public ResultMessageVo withdrawalCount(Long uid) {
        SysConfigMo sysConfigMo = sysConfigService.getSysConfigMoByKey("WITHDRAWAL");
        BalanceMo balanceMo = balanceService.selectByUserId(uid, 1);
        Map<String, BigDecimal> map = new HashMap<>();
        if (sysConfigMo.getConfigType().equals("Y")) {
            Gson gson = new Gson();
            WithdrawalMo withdrawalMo = gson.fromJson(sysConfigMo.getConfigValue(), WithdrawalMo.class);
            BigDecimal surplusWithdrawal = orderMapper.selectSurplusWithdrawal(uid);
            if (null == surplusWithdrawal) {
                map.put("minWithdrawal", new BigDecimal(withdrawalMo.getLowAmount()));
                map.put("maxWithdrawal", new BigDecimal("0.00"));
                return ResultUtil.data(map);
            }
            BigDecimal percent = new BigDecimal(withdrawalMo.getWithdrawalAmountPercent()).divide(new BigDecimal("100"),
                2, BigDecimal.ROUND_DOWN);
            surplusWithdrawal = surplusWithdrawal.multiply(percent).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal maxWithdrawal =
                surplusWithdrawal.compareTo(balanceMo.getBalance()) > 0 ? balanceMo.getBalance() : surplusWithdrawal;
            map.put("minWithdrawal", new BigDecimal(withdrawalMo.getLowAmount()));
            map.put("maxWithdrawal", maxWithdrawal);
            return ResultUtil.data(map);
        }
        map.put("minWithdrawal", new BigDecimal("1.00"));
        map.put("maxWithdrawal", balanceMo.getBalance());
        return ResultUtil.data(map);
    }
}
