package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.DepositConfigHistoryMo;
import com.ulla.modules.assets.vo.BonusVo;

/**
 * <p>
 * 入金配置表 服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
public interface DepositConfigHistoryService extends IService<DepositConfigHistoryMo> {

    /**
     * 获取所有入金配置
     * @return
     */
    ResultMessageVo getAllDepositConfig();

    /**
     * 根据金额、用户id、订单创建时间来提供入金奖金、新人活动奖金、总奖金、配置id
     * @param amount 金额
     * @param userId 数据库使用的用户id
     * @param orderCreateTime 订单的创建时间（或当前时间）
     * @return
     */
    ResultMessageVo<BonusVo> getDepositBonus(Integer amount, Long userId, Long orderCreateTime);
}
