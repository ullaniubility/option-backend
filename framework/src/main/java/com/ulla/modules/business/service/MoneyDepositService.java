package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.MoneyDepositMo;
import com.ulla.modules.business.qo.MoneyDepositQo;
import com.ulla.modules.business.qo.UserWithdrawalQo;

public interface MoneyDepositService extends IService<MoneyDepositMo> {
    ResultMessageVo expenditureList(MoneyDepositQo qo);

    ResultMessageVo userWithdrawal(UserWithdrawalQo userWithdrawalQo, Long uid, String openId);

    ResultMessageVo withdrawalCount(Long uid);
}
