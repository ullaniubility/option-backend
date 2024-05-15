package com.ulla.modules.payment.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.vo.MoneyPaymentTransactionVO;
import com.ulla.modules.payment.vo.TransactionParamerVO;

/**
 * 入金订单
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-02-27 18:14:50
 */
@Mapper
public interface MoneyPaymentTransactionMapper extends BaseMapper<MoneyPaymentTransactionEntity> {

    /**
     * 根据订单Id获取订单详情
     * 
     * @param orderId
     *            订单Id
     * @return
     */
    public MoneyPaymentTransactionEntity getTransactionByOrderId(@Param("orderId") String orderId);

    /**
     * 订单详情里加上wallectAddress
     */
    public MoneyPaymentTransactionVO getWallectByOrderId(@Param("orderId") String orderId);

    /**
     * 按条件筛选入金订单信息
     * 
     * @return
     */
    List<TransactionParamerVO> getTransactionListByParamer(TransactionParamerVO vo);

    Integer getTransactionListByParamerCount(TransactionParamerVO vo);

    /**
     * 处理失效订单 - 订单时长超过30分钟切状态仍在待付款状态订单处理为失效订单
     * 
     * @param endTime
     * @return
     */
    public List<MoneyPaymentTransactionEntity> getProcessInvalidOrders(@Param("endTime") Long endTime);

    @Select("SELECT IFNULL(SUM(estimated_deposit_amount), 0) AS orderAmountCount FROM money_payment_transaction WHERE uid = #{uid} AND order_status =2")
    BigDecimal getOrderAmountCount(Long uid);

    @Select("SELECT * from money_payment_transaction where open_id = #{openId} and (if_read = 1 or if_read =0) and order_status = 2")
    List<MoneyPaymentTransactionEntity> getNoReadList(String openId);
}
