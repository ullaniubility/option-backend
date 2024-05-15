package com.ulla.modules.assets.service.impl;

import static com.ulla.constant.NumberConstant.TWO;
import static com.ulla.constant.NumberConstant.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.constants.ActiveConstant;
import com.ulla.modules.assets.mapper.DepositConfigHistoryMapper;
import com.ulla.modules.assets.mo.DepositConfigHistoryMo;
import com.ulla.modules.assets.service.DepositConfigHistoryService;
import com.ulla.modules.assets.service.DepositConfigService;
import com.ulla.modules.assets.vo.BonusVo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.service.UserService;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 入金配置表 服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DepositConfigHistoryServiceImpl extends ServiceImpl<DepositConfigHistoryMapper, DepositConfigHistoryMo>
    implements DepositConfigHistoryService {

    final DepositConfigService depositConfigService;
    final UserService userService;

    @Override
    public ResultMessageVo getAllDepositConfig() {
        return ResultUtil.data(baseMapper.getAllDepositConfig(null));
    }

    @Override
    public ResultMessageVo<BonusVo> getDepositBonus(Integer amount, Long userId, Long orderCreateTime) {
        // 获取金额区间内的入金配置
        List<DepositConfigHistoryMo> allDepositConfig = baseMapper.getLatestConfiguration(amount);
        if (CollectionUtils.isEmpty(allDepositConfig)) {
            return ResultUtil.error(ResultCodeEnums.FREIGHT_TEMPLATE_NOT_EXIST);
        }
        BonusVo bonusVo = new BonusVo();
        DepositConfigHistoryMo depositConfigHistoryMo = allDepositConfig.get(ZERO);
        // 获取用户信息并且校验
        UserMo user = userService.getById(userId);
        if (ObjectUtil.isEmpty(user)) {
            return ResultUtil.error(ResultCodeEnums.USER_NOT_EXIST);
        }
        bonusVo.setFirstDepositFlag(user.getFirstDepositFlag());
        /*// 新人入金标识
        boolean firstDepositFlag = BooleanUtils.toBoolean(user.getFirstDepositFlag());
        // 新人入金活动开启标识
        boolean newcomerActiveFlag = BooleanUtils.toBoolean(depositConfigHistoryMo.getNewcomerActiveFlag());
        //如果新人活动开启，则判断是否在新人活动时间内
        if (newcomerActiveFlag) {
            Long createTime = user.getCreateTime();
            long activeTime = depositConfigHistoryMo.getActiveTime() * T60T * T1000L;
            newcomerActiveFlag = createTime + activeTime > orderCreateTime;
        }
        bonusVo.setBonus(BigDecimal.ZERO);
        if (!firstDepositFlag && newcomerActiveFlag) {
            // 计算新人活动奖励金额
            Integer activeRewardModel = depositConfigHistoryMo.getActiveRewardModel();
            bonusVo.setBonus(BigDecimal.valueOf(depositConfigHistoryMo.getActiveRewardAmount()));
            if (ActiveConstant.PERCENTAGE_MODEL.equals(activeRewardModel)) {
                BigDecimal percentage = BigDecimal.valueOf(depositConfigHistoryMo.getActiveRewardAmount())
                    .divide(BigDecimal.TEN.multiply(BigDecimal.TEN), TWO, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(amount)).setScale(TWO, RoundingMode.HALF_UP);
                bonusVo.setBonus(percentage);
            }
        } else {*/
        // 计算配置金额
        Integer rewardModel = depositConfigHistoryMo.getRewardModel();
        bonusVo.setBonus(BigDecimal.valueOf(depositConfigHistoryMo.getRewardAmount()));
        if (ActiveConstant.PERCENTAGE_MODEL.equals(rewardModel)) {
            BigDecimal percentage = BigDecimal.valueOf(depositConfigHistoryMo.getRewardAmount())
                .divide(BigDecimal.TEN.multiply(BigDecimal.TEN), TWO, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(amount)).setScale(TWO, RoundingMode.HALF_UP);
            bonusVo.setBonus(percentage);
        }
        // }
        String configId =
            depositConfigHistoryMo.getDepositId() == null ? "-1" : depositConfigHistoryMo.getDepositId().toString();
        bonusVo.setDepositId(Long.valueOf(configId));
        return ResultUtil.data(bonusVo);
    }
}
