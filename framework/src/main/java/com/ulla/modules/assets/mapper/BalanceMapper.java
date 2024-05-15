package com.ulla.modules.assets.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.assets.mo.BalanceMo;
import com.ulla.modules.assets.vo.DepositOrderVo;

public interface BalanceMapper extends BaseMapper<BalanceMo> {

    @Select("select * from biz_balance where user_id = #{userId} and type = #{type} ")
    BalanceMo selectByUserId(Long userId, Integer type);

    @Update("UPDATE `biz_balance` SET `balance` = balance + #{balanceMo.balance} WHERE user_id = #{balanceMo.userId} and type = #{balanceMo.type}")
    Long updateBalance(@Param("balanceMo") BalanceMo balanceMo);

    List<DepositOrderVo> getDepositAmount(@Param("userId") Long userId);

    @Select("SELECT SUM(CASE type WHEN 0 THEN balance ELSE 0 END) as bonusBalance, "
        + "SUM(CASE type WHEN 1 THEN balance ELSE 0 END) as realBalance, "
        + "SUM(CASE type WHEN 3 THEN balance ELSE 0 END) as virtualBalance "
        + "FROM biz_balance WHERE user_id = #{userId} GROUP BY user_id")
    Map<String, BigDecimal> getWallet(Long userId);
}
