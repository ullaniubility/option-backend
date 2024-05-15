package com.ulla.modules.business.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.admin.qo.ConditionQo;
import com.ulla.modules.business.qo.BalanceQo;
import com.ulla.modules.business.qo.FinanceQo;
import com.ulla.modules.business.qo.HistoryQo;
import com.ulla.modules.business.vo.*;

public interface MoneyHistoryMapper extends BaseMapper<RechargeVo> {

    @Select("(SELECT d.uid,d.create_time,d.order_status,null as net,d.deposit_monetary_amount,null as estimated_deposit_amount, d.remark,null as rechargeType,null as reward_amount FROM `money_deposit` d  WHERE d.uid=#{uid}) UNION ALL(SELECT t.uid,t.create_time,null as order_status,t.net,null as deposit_monetary_amount,t.estimated_deposit_amount,null as remark, t.channel_name as rechargeType,t.reward_amount FROM  money_payment_transaction t   WHERE t.uid=#{uid} and (t.order_status=1 OR t.order_status=2))")
    IPage<RechargeVo> selectHistory(Page<HistoryQo> page, Long uid);

    IPage<FinanceVo> financeHistory(Page<FinanceQo> page, @Param("ew") QueryWrapper<FinanceVo> queryWrapper);

    IPage<BalanceVo> balanceHistory(Page<BalanceQo> page, @Param("ew") QueryWrapper<BalanceVo> wrapper);

    @Select("SELECT SUM(currency_amount) as todayEnterAmount FROM `money_payment_transaction` WHERE (order_status=1 OR order_status=2) and create_time BETWEEN #{todayEnterBeginTime} AND #{todayEnterEndTime}")
    BigDecimal actualTransaction(long todayEnterBeginTime, Long todayEnterEndTime);

    @Select("SELECT SUM(currency_amount) as weekEnterAmount FROM `money_payment_transaction` WHERE (order_status=1 OR order_status=2) and create_time BETWEEN #{weekEnterBeginTime} AND #{weekEnterEndTime}")
    BigDecimal selectWeekEnter(Long weekEnterBeginTime, Long weekEnterEndTime);

    @Select("SELECT * FROM `biz_order` WHERE ((`status`=2  AND if_profit=0 AND type=1 ) OR (`status`=3  AND if_profit=1 AND type=1) )")
    List<PairVo> percentage();

    List<YearChartVo> bar(@Param("sy") String symbol, @Param("sq") String net);

    @Select("SELECT FROM_UNIXTIME(create_time/1000,'%Y%m') months,COUNT(id) count,SUM(currency_amount)\n"
        + "        currencyAmount,symbol,net FROM `money_payment_transaction` GROUP BY months")
    List<YearChartVo> barAll();

    @Select("SELECT SUM(deposit_monetary_amount) as todayOutAmount FROM `money_deposit` WHERE (order_status=1 OR order_status=2) and create_time BETWEEN #{todayEnterBeginTime} AND #{todayEnterEndTime}")
    BigDecimal depositMoney(long todayEnterBeginTime, Long todayEnterEndTime);

    @Select("SELECT SUM(deposit_monetary_amount) as todayOutAmount FROM `money_deposit` WHERE (order_status=1 OR order_status=2) and create_time BETWEEN #{weekEnterBeginTime} AND #{weekEnterEndTime}")
    BigDecimal selectWeekOut(Long weekEnterBeginTime, Long weekEnterEndTime);

    @Select("SELECT COUNT(*) as outTotal FROM money_deposit WHERE order_status=0")
    Integer selectWait();

    @Select("SELECT COUNT(*) as enterTotal FROM money_payment_transaction WHERE order_status=1")
    Integer selectEnterWait();

    @Select("SELECT DISTINCT net,symbol FROM money_payment_transaction")
    List<ConditionQo> getCondition();

    @Select("(SELECT null as type,null as eo_point,d.uid,d.create_time,d.order_status,null as net,d.deposit_monetary_amount,null as estimated_deposit_amount, d.remark,null as rechargeType,null as reward_amount FROM `money_deposit` d  WHERE d.uid=#{uid}) UNION ALL(SELECT null as type,null as eo_point,t.uid,t.create_time,null as order_status,t.net,null as deposit_monetary_amount,t.estimated_deposit_amount,null as remark, t.channel_name as rechargeType,t.reward_amount FROM  money_payment_transaction t   WHERE t.uid=#{uid} and (t.order_status=1 OR t.order_status=2)) UNION ALL(SELECT e.type,e.eo_point/100,e.uid,e.create_time,null as order_status,null as net,null as deposit_monetary_amount,null as estimated_deposit_amount,null as remark,null as rechargeType,null as reward_amount FROM biz_eo_point_log e WHERE e.eo_point<0 and e.uid=#{uid} AND (e.delete_flag=0 OR e.delete_flag is null)) ORDER BY create_time desc")
    List<RechargeVo> selectHistoryList(Long uid);
}
