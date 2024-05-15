package com.ulla.modules.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.entity.ReasonReissueInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 入金订单补发理由信息表
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-03-21 14:21:35
 */
@Mapper
public interface ReasonReissueInfoMapper extends BaseMapper<ReasonReissueInfoEntity> {

    /**
     * 根据订单Id获取订单详情
     * @param orderId 订单Id
     * @return
     */
    public List<ReasonReissueInfoEntity> getReasonReissueList(@Param("orderId")  String orderId);

}
