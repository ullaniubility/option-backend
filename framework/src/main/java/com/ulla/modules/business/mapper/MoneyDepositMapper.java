package com.ulla.modules.business.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.business.mo.MoneyDepositMo;
import com.ulla.modules.business.qo.MoneyDepositQo;

@Mapper
public interface MoneyDepositMapper extends BaseMapper<MoneyDepositMo> {

    List<MoneyDepositMo> getExpenditureList(MoneyDepositQo qo);

    Integer getExpenditureListCount(MoneyDepositQo qo);

    @Select("SELECT IFNULL(SUM(deposit_monetary_amount),0) AS withdrawalCount FROM money_deposit WHERE uid = #{uid} AND order_status != 3")
    BigDecimal withdrawalCount(Long uid);
}
