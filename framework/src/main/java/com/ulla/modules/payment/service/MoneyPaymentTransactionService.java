package com.ulla.modules.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.vo.TransactionParamerVO;

/**
 * 入金订单
 *
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-02-27 18:14:50
 */
public interface MoneyPaymentTransactionService extends IService<MoneyPaymentTransactionEntity> {

    /**
     * 入金订单列表查询
     *
     * @param vo 列表查询参数
     * @return
     */
    public ResultMessageVo transactionListByParamer(TransactionParamerVO vo);

    /**
     * 入金订单详情查询
     *
     * @param orderId 订单Id
     * @return
     */
    public ResultMessageVo getTransactionInfoById(String orderId);

    /**
     * 订单失效或者失败时 一键补发操作
     *
     * @param orderId 订单Id
     * @return
     */
    public ResultMessageVo supplyAgain(String orderId, String reasonInfo);

    /**
     * 查询入金订单是否充值成功
     *
     * @param uid 用户Id
     * @return
     */
    public ResultMessageVo getDepositAmount(Long uid);

    ResultMessageVo getDepositAmount(String openId);

    ResultMessageVo getWallectByOrderId(String orderId);
}
