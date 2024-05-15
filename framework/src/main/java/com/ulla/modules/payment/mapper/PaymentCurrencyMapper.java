package com.ulla.modules.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.entity.PaymentCurrencyEntity;
import com.ulla.modules.payment.vo.TransactionParamerVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 入金支付功能支持的币种
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-03-13 16:47:19
 */
@Mapper
public interface PaymentCurrencyMapper extends BaseMapper<PaymentCurrencyEntity> {


    /**
     * 入金支付 - 获取可支付币种列表
     * @return
     */
    List<PaymentCurrencyEntity> getPaymentCurrency();


	
}
