package com.ulla.modules.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.entity.SysRateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统汇率表
 * 
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-03-04 10:41:52
 */
@Mapper
public interface SysRateMapper extends BaseMapper<SysRateEntity> {

    /**
     * 根据货币符号获取货币对应美元的汇率
     * @param symbol 货币单位
     * @return
     */
    public SysRateEntity getRateByUnit(@Param("symbol")  String symbol);

}
