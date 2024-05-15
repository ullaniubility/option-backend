package com.ulla.modules.payment.service;

import java.util.Map;

import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.vo.FigureCurrencyParamerVO;
import com.ulla.modules.payment.vo.UpdatePayStatusVo;

/**
 * 入金功能
 *
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-02-25 14:51:23
 */
public interface MoneyPaymentService {

    ResultMessageVo createFigureCurrency(FigureCurrencyParamerVO vo);

    ResultMessageVo figureCurrencyRecharge(Map<String, Object> map);

    ResultMessageVo updateLegalCurrency(UpdatePayStatusVo vo);

    ResultMessageVo updateRewardAmount(MoneyPaymentTransactionEntity vo);
}
