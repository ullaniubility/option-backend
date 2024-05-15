package com.ulla.modules.assets.service.impl;

import static com.ulla.constant.NumberConstant.ONE;
import static com.ulla.constant.NumberConstant.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.BusinessNoUtil;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.common.vo.exception.ServiceException;
import com.ulla.modules.admin.mo.NewUserAmountMo;
import com.ulla.modules.admin.mo.SysConfigMo;
import com.ulla.modules.admin.service.SysConfigService;
import com.ulla.modules.assets.enums.BalanceTypeEnums;
import com.ulla.modules.assets.mapper.BalanceMapper;
import com.ulla.modules.assets.mo.BalanceErrorLogMo;
import com.ulla.modules.assets.mo.BalanceLogMo;
import com.ulla.modules.assets.mo.BalanceMo;
import com.ulla.modules.assets.service.BalanceErrorLogService;
import com.ulla.modules.assets.service.BalanceLogService;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.assets.vo.DepositOrderVo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.service.UserService;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 1
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BalanceServiceImpl extends ServiceImpl<BalanceMapper, BalanceMo> implements BalanceService {

    final BalanceLogService balanceLogService;
    final UserService userService;
    final BalanceErrorLogService balanceErrorLogService;
    final SysConfigService sysConfigService;

    @Override
    public ResultMessageVo<Map<String, String>> getUserBalance(Long uid) {
        List<BalanceMo> balanceMos = selectElseSaveBalance(uid);
        if (balanceMos.size() == 0) {
            return ResultUtil.error(ResultCodeEnums.USER_NOT_EXIST);
        }
        Map<Integer, BalanceMo> balanceMoMap =
            balanceMos.stream().collect(Collectors.toMap(BalanceMo::getType, s -> s));
        BalanceMo realBalanceMo = balanceMoMap.get(BalanceTypeEnums.REAL_BALANCE.getType());
        BigDecimal realBalance = realBalanceMo.getBalance().add(realBalanceMo.getBlockedBalance());
        BalanceMo bonusBalanceMo = balanceMoMap.get(BalanceTypeEnums.BONUS_BALANCE.getType());
        BigDecimal bonusBalance = bonusBalanceMo.getBalance().add(bonusBalanceMo.getBlockedBalance());
        BalanceMo virtualBalanceMo = balanceMoMap.get(BalanceTypeEnums.VIRTUAL_BALANCE.getType());
        BigDecimal virtualBalance = virtualBalanceMo.getBalance().add(bonusBalanceMo.getBlockedBalance());
        Map<String, String> map = new HashMap<>(3);
        map.put("realBalance", realBalance.setScale(2, RoundingMode.HALF_UP).toPlainString());
        map.put("bonusBalance", bonusBalance.setScale(2, RoundingMode.HALF_UP).toPlainString());
        map.put("virtualBalance", virtualBalance.setScale(2, RoundingMode.HALF_UP).toPlainString());
        return ResultUtil.data(map);
    }

    @Override
    public Boolean checkOrderAndChangeBalance(BalanceChangeVo vo) {
        boolean b = true;
        switch (vo.getBusinessTypeEnums()) {
            case DEPOSIT:
                UserMo userMo = userService.getById(vo.getUid());
                // 判断用户是否已首冲
                if (BooleanUtils.toBoolean(userMo.getFirstDepositFlag())) {
                    b = false;
                }
                UserMo user = new UserMo();
                user.setUid(userMo.getUid());
                user.setFirstDepositFlag(ONE);
                userService.updateById(user);
                // 入金金额必须大于0,奖金必须大于或等于0
                if (vo.getAmount().compareTo(BigDecimal.ZERO) <= ZERO
                    || vo.getBonusAmount().compareTo(BigDecimal.ZERO) < ZERO) {
                    throw new ServiceException("金额错误");
                }
                break;
            case EXCHANGE:
                BigDecimal orderAmount = vo.getAmount();
                vo.setAmount(null);
                BalanceMo realBalance = baseMapper.selectByUserId(vo.getUid(), 1);
                if (null != orderAmount && orderAmount.compareTo(realBalance.getBalance()) > 0) {
                    BalanceMo bonusBalance = baseMapper.selectByUserId(vo.getUid(), 0);
                    BigDecimal totalBalance = realBalance.getBalance().compareTo(new BigDecimal("0.0")) > 0
                        ? realBalance.getBalance().add(bonusBalance.getBalance()) : bonusBalance.getBalance();
                    // 余额不足直接返回
                    if (orderAmount.compareTo(totalBalance) > 0) {
                        throw new ServiceException("余额不足");
                    } else {
                        if (realBalance.getBalance().compareTo(new BigDecimal("00000000000000000.00")) > 0) {
                            orderAmount = orderAmount.subtract(realBalance.getBalance());
                            vo.setAmount(BigDecimal.ZERO.subtract(realBalance.getBalance()));
                        }
                        vo.setBonusAmount(BigDecimal.ZERO.subtract(orderAmount));
                    }
                } else if (null != orderAmount) {
                    vo.setAmount(BigDecimal.ZERO.subtract(orderAmount));
                }
                break;
            default:
                break;
        }
        try {
            // 创建新事务修改金额和记录日志不影响订单流程
            changeBalanceAndSaveLog(vo);
        } catch (Exception e) {
            log.error("=========================================警告：修改用户余额失败！！！==================================");
            log.error(e.getMessage());
            String remark =
                String.format("用户：{}的{}订单修改余额失败，订单号：{}，金额：{}，奖金：{}", vo.getUid(), vo.getBusinessTypeEnums().getDesc(),
                    vo.getBusinessNo(), vo.getAmount().toPlainString(), vo.getBonusAmount().toPlainString());

            BalanceErrorLogMo errorLogMo = BalanceErrorLogMo.builder().userId(vo.getUid()).amount(vo.getAmount())
                .bonusAmount(vo.getBonusAmount()).businessType(vo.getBusinessTypeEnums().getType())
                .businessNo(vo.getBusinessNo()).disposeFlag(ZERO).remark(remark).build();
            try {
                balanceErrorLogService.newTransactionalSave(errorLogMo);
            } catch (Exception ex) {
                log.error("=========================================警告：记录错误日志失败！！！==================================");
                log.error(remark);
            }
        }
        return b;
    }

    /**
     * 修改余额并且添加日志
     * 
     * @param balanceChangeVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean changeBalanceAndSaveLog(BalanceChangeVo balanceChangeVo) {
        Long userId = balanceChangeVo.getUid();
        List<BalanceMo> oldBalances = selectElseSaveBalance(userId);// 获取用户名下所有的余额，会按资金类型分类，会有多个
        List<BalanceTypeEnums> operableBalanceType = balanceChangeVo.getBusinessTypeEnums().getOperableBalanceType();
        try {
            operableBalanceType.forEach(balanceType -> {
                BigDecimal amount = BigDecimal.ZERO;
                switch (balanceType) {
                    case REAL_BALANCE:
                        if (oldBalances.stream().filter(v -> v.getType().intValue() == 1).allMatch(
                            t -> t.getBalance().add(balanceChangeVo.getAmount()).compareTo(BigDecimal.ZERO) < 0)) {
                            return;
                        }
                        amount = balanceChangeVo.getAmount();
                        break;
                    case BONUS_BALANCE:
                        if (oldBalances.stream().filter(v -> v.getType().intValue() == 0).allMatch(
                            t -> t.getBalance().add(balanceChangeVo.getAmount()).compareTo(BigDecimal.ZERO) < 0)) {
                            return;
                        }
                        amount = balanceChangeVo.getBonusAmount();
                        break;
                    // todo 机器人余额和虚拟余额暂不处理
                    case ROBOT_BALANCE:
                    case VIRTUAL_BALANCE:
                        if (oldBalances.stream().filter(v -> v.getType().intValue() == 3).allMatch(
                            t -> t.getBalance().add(balanceChangeVo.getAmount()).compareTo(BigDecimal.ZERO) < 0)) {
                            return;
                        }
                        amount = balanceChangeVo.getVirtualAmount();
                        break;
                    default:
                        break;
                }
                // 0金额不处理
                if (ObjectUtil.isEmpty(amount) || amount.compareTo(BigDecimal.ZERO) == ZERO) {
                    return;
                }
                BalanceMo newBalance = BalanceMo.builder().balance(amount).blockedBalance(BigDecimal.ZERO)
                    .userId(balanceChangeVo.getUid()).type(balanceType.getType()).build();
                baseMapper.updateBalance(newBalance);
                BalanceMo oldBalance =
                    oldBalances.stream().filter(b -> b.getType().equals(balanceType.getType())).findFirst().get();
                String remark = null;
                if (StringUtils.isNotBlank(balanceChangeVo.getRemark())) {
                    remark = balanceChangeVo.getBusinessTypeEnums().getDesc() + balanceType.getDesc() + ","
                        + balanceChangeVo.getRemark();
                } else {
                    remark = balanceChangeVo.getBusinessTypeEnums().getDesc() + balanceType.getDesc();
                }
                BalanceLogMo balanceLogMo = BalanceLogMo.builder()
                    .logNo(BusinessNoUtil.genBusinessNo(balanceChangeVo.getBusinessTypeEnums().getPrefix()))
                    .balance(oldBalance.getBalance().add(amount)).amount(amount)
                    .businessType(balanceChangeVo.getBusinessTypeEnums().getType())
                    .businessNo(balanceChangeVo.getBusinessNo()).type(balanceType.getType()).remark(remark).build();
                balanceLogService.createLog(balanceLogMo, userId);
            });
        } catch (ServiceException e) {
            log.error("{}订单余额变动失败，用户id：{}，订单编号：{}", balanceChangeVo.getBusinessTypeEnums().getDesc(), userId,
                balanceChangeVo.getBusinessNo());
            log.error(e.getMsg());
            throw new ServiceException(e.getMsg());
        }
        return Boolean.TRUE;
    }

    /**
     * 交易修改余额并且添加日志
     *
     * @param balanceChangeVo
     * @return
     */
    @Override
    public ResultMessageVo transactionChangeBalanceAndSaveLog(BalanceChangeVo balanceChangeVo) {
        Long userId = balanceChangeVo.getUid();
        try {
            LambdaQueryWrapper<BalanceMo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BalanceMo::getUserId, userId);
            List<BalanceMo> balanceMoList = baseMapper.selectList(wrapper);
            balanceMoList.forEach(balanceMo -> {
                BalanceTypeEnums balanceTypeEnums = null;
                BigDecimal amount = BigDecimal.ZERO;
                int balanceType = balanceMo.getType();
                switch (balanceType) {
                    case 0:
                        if (null == balanceChangeVo.getBonusAmount() || balanceMo.getBalance()
                            .add(balanceChangeVo.getBonusAmount()).compareTo(BigDecimal.ZERO) < 0) {
                            break;
                        }
                        balanceTypeEnums = BalanceTypeEnums.BONUS_BALANCE;
                        amount = balanceChangeVo.getBonusAmount();
                        break;
                    case 1:
                        if (null == balanceChangeVo.getAmount()
                            || balanceMo.getBalance().add(balanceChangeVo.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
                            break;
                        }
                        balanceTypeEnums = BalanceTypeEnums.REAL_BALANCE;
                        amount = balanceChangeVo.getAmount();
                        break;
                    case 3:
                        if (null == balanceChangeVo.getVirtualAmount()) {
                            break;
                        }
                        int flag =
                            balanceMo.getBalance().add(balanceChangeVo.getVirtualAmount()).compareTo(BigDecimal.ZERO);
                        if (flag <= 0) {
                            QueryWrapper<SysConfigMo> updateWrapper = new QueryWrapper<>();
                            updateWrapper.eq("config_key", "NEW_USER_AMOUNT");
                            NewUserAmountMo newUserAmountMo = JSONObject.parseObject(
                                sysConfigService.getOne(updateWrapper).getConfigValue(), NewUserAmountMo.class);
                            if (BooleanUtils.toBoolean(newUserAmountMo.getInfiniteReset())) {
                                amount =
                                    balanceChangeVo.getVirtualAmount().add(new BigDecimal(newUserAmountMo.getAmount()));
                            } else if (flag < 0) {
                                throw new ServiceException(ResultCodeEnums.VIRTUAL_AMOUNT_INSUFFICIENT);
                            } else {
                                amount = balanceChangeVo.getVirtualAmount();
                            }
                        } else {
                            amount = balanceChangeVo.getVirtualAmount();
                        }
                        balanceTypeEnums = BalanceTypeEnums.VIRTUAL_BALANCE;
                        break;
                    default:
                        break;
                }
                // 0金额不处理
                if (ObjectUtil.isEmpty(amount) || amount.compareTo(BigDecimal.ZERO) == ZERO) {
                    return;
                }
                BalanceMo newBalance = BalanceMo.builder().balance(amount).blockedBalance(BigDecimal.ZERO)
                    .userId(balanceChangeVo.getUid()).type(balanceType).build();
                baseMapper.updateBalance(newBalance);
                String remark = null;
                if (StringUtils.isNotBlank(balanceChangeVo.getRemark())) {

                    remark = balanceChangeVo.getBusinessTypeEnums().getDesc() + balanceTypeEnums.getDesc() + ","
                        + balanceChangeVo.getRemark();
                } else {
                    remark = balanceChangeVo.getBusinessTypeEnums().getDesc() + balanceTypeEnums.getDesc();
                }
                BalanceLogMo balanceLogMo = BalanceLogMo.builder()
                    .logNo(BusinessNoUtil.genBusinessNo(balanceChangeVo.getBusinessTypeEnums().getPrefix()))
                    .balance(balanceMo.getBalance().add(amount)).amount(amount)
                    .businessType(balanceChangeVo.getBusinessTypeEnums().getType())
                    .businessNo(balanceChangeVo.getBusinessNo()).type(balanceTypeEnums.getType()).remark(remark)
                    .build();
                balanceLogService.createLog(balanceLogMo, userId);
            });
        } catch (ServiceException e) {
            log.error("{}订单余额变动失败，用户id：{}，订单编号：{}", balanceChangeVo.getBusinessTypeEnums().getDesc(), userId,
                balanceChangeVo.getBusinessNo());
            log.error(e.getMsg());
            throw new ServiceException(e.getMsg());
        }
        return ResultUtil.data(baseMapper.getWallet(userId));
    }

    @Override
    public List<DepositOrderVo> getDepositAmount(Long userId) {
        return baseMapper.getDepositAmount(userId);
    }

    @Override
    public BalanceMo selectByUserId(Long userId, Integer type) {
        return baseMapper.selectByUserId(userId, type);
    }

    private List<BalanceMo> selectElseSaveBalance(Long userId) {
        // 获取用户的资产
        List<BalanceMo> balanceMos = list(new LambdaQueryWrapper<BalanceMo>().eq(BalanceMo::getUserId, userId));
        List<BalanceMo> addBalanceMos = new ArrayList<>(2);
        List<Integer> userTypes = BalanceTypeEnums.getUserType();// 获取余额类型 0。奖励金额1。真实资金 2。机器人资金 3、虚拟资金
        if (CollectionUtils.isEmpty(balanceMos)) {// 余额为空时的处理
            userTypes.forEach(type -> {
                BalanceMo balanceMo = BalanceMo.builder().type(type).userId(userId).balance(BigDecimal.ZERO)
                    .blockedBalance(BigDecimal.ZERO).build();
                if (type.intValue() == 3) {
                    QueryWrapper<SysConfigMo> updateWrapper = new QueryWrapper<>();
                    updateWrapper.eq("config_key", "NEW_USER_AMOUNT");
                    NewUserAmountMo newUserAmountMo = JSONObject
                        .parseObject(sysConfigService.getOne(updateWrapper).getConfigValue(), NewUserAmountMo.class);
                    balanceMo.setBalance(new BigDecimal(newUserAmountMo.getAmount()));
                }
                balanceMos.add(balanceMo);
                addBalanceMos.add(balanceMo);
            });
        } else {
            userTypes.forEach(type -> {
                boolean b = balanceMos.stream().anyMatch(balanceMo -> balanceMo.getType().equals(type));
                if (!b) {
                    BalanceMo balanceMo = BalanceMo.builder().type(type).userId(userId).balance(BigDecimal.ZERO)
                        .blockedBalance(BigDecimal.ZERO).build();
                    balanceMos.add(balanceMo);
                    addBalanceMos.add(balanceMo);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(addBalanceMos)) {
            saveBatch(addBalanceMos);
        }
        return balanceMos;
    }
}
