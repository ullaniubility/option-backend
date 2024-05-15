package com.ulla.modules.assets.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.BalanceMo;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.assets.vo.DepositOrderVo;

/**
 * @author {clj}
 * @Description {订单service}
 * @since {2023-2-21}
 */
public interface BalanceService extends IService<BalanceMo> {
    /**
     * 获得用户资产，若不存在，则创建余额数据返回
     * 
     * @return
     */
    ResultMessageVo<Map<String, String>> getUserBalance(Long uid);

    /**
     * 订单成功调用改变首充状态和改变余额
     * 
     * @param balanceChangeVo
     * @return 返回是否首充，false不是首充订单，返回true是首充订单
     */
    Boolean checkOrderAndChangeBalance(BalanceChangeVo balanceChangeVo);

    /**
     * 余额变动和记录日志的通用方法
     * 
     * @param balanceChangeVo
     * @return
     */
    Boolean changeBalanceAndSaveLog(BalanceChangeVo balanceChangeVo);

    ResultMessageVo transactionChangeBalanceAndSaveLog(BalanceChangeVo balanceChangeVo);

    List<DepositOrderVo> getDepositAmount(Long userId);

    BalanceMo selectByUserId(Long userId, Integer type);
}
