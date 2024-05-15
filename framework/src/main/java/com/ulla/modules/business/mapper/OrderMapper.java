package com.ulla.modules.business.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.binance.vo.QuotationProductVo;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.qo.MarketAnalysisQo;
import com.ulla.modules.business.qo.OrderHistoryQo;
import com.ulla.modules.business.qo.OrderQo;
import com.ulla.modules.business.vo.*;

public interface OrderMapper extends BaseMapper<OrderMo> {

    MarketAnalysisVo selectMonth(@Param("query") MarketAnalysisQo marketAnalysisQo);

    List<MarketAnalysisVo.Transaction> selectTransaction(@Param("query") MarketAnalysisQo marketAnalysisQo);

    List<MarketAnalysisVo.MarketAnalysis> selectPercentage(@Param("query") MarketAnalysisQo marketAnalysisQo);

    IPage<OrderSearchVo> simulationOrder(Page<OrderQo> page, @Param("query") OrderQo qo);

    List<OrderCalculationVo> orderCalculationListByUid(Long uid);

    @Select("SELECT id,stream_time, symbol, k_start_time, k_end_time, close_price, open_price, "
        + "high_price, low_price, turnover_num, turnover_amount, tran_limit, close_flag, uuid, "
        + "trading_range_id, order_range_id, order_start_time, order_end_time, trading_start_time, "
        + "trading_end_time  FROM ba_quotation_kline_${Symbol} WHERE stream_time = #{streamTime}")
    QuotationProductVo getQuotationByStreamTime(String Symbol, Long streamTime);

    @Select("select COUNT(order_amount) as openOrder,SUM(order_amount) as openAmount from biz_order WHERE (delete_flag != 1 or delete_flag is null)")
    HomeUserVo getOpenOrder();

    IPage<OrderHistoryVo> recentTransactions(Page<OrderHistoryQo> initPage,
        @Param(Constants.WRAPPER) QueryWrapper<OrderHistoryQo> queryWrapper);

    @Select("SELECT date_add(#{startTime}, INTERVAL ( cast( id AS signed INTEGER ) - DAY ( #{overTime} )) + 1 DAY )AS dateFlag FROM sys_assist")
    List<String> selectTime(String startTime, String overTime);

    @Select("SELECT #{symbol}, COUNT( DISTINCT order_code, 1 ) AS orderCount,"
        + "COUNT( DISTINCT uid, 1 ) AS userCount, IFNULL( SUM( order_amount ), 0 ) AS orderTotalAmount, "
        + "IFNULL( ROUND( SUM( IF ( open_close = 1, 1, 0 ))/ COUNT( 1 )* 100, 0 ), 0 ) AS risePercent, "
        + "IFNULL( ROUND( SUM( IF ( open_close = 0, 1, 0 ))/ COUNT( 1 )* 100, 0 ), 0 ) AS dropPercent  FROM "
        + "biz_order  WHERE pairs = #{symbol}  AND order_time >= #{beforeStartTime}  AND order_time <= #{minuteEndTime}")
    OrderRankBySymbolVo orderRankBySymbol(String symbol, Long beforeStartTime, Long minuteEndTime);

    List<MarketAnalysisVo.MarketAnalysis> selectCountPairs(@Param("query") MarketAnalysisQo marketAnalysisQo);

    Integer selectAllCount(@Param("query") MarketAnalysisQo marketAnalysisQo);

    @Select("SELECT sum(IF( withdrawal_amount IS NULL OR withdrawal_amount = '', 0, withdrawal_amount )) AS surplusWithdrawal "
        + "FROM biz_order WHERE uid = #{uid} AND status = 2 AND type = 1")
    BigDecimal selectSurplusWithdrawal(Long uid);

    @Select("SELECT FROM_UNIXTIME(o.order_time/1000,'%Y%m') months,COUNT(o.id) count,SUM(o.order_amount) orderAmount,SUM(o.benefit) benefit FROM biz_order o  LEFT JOIN qa_transaction_category_child c ON o.pairs_id=c.id GROUP BY months")
    List<YearChartVo> barAllOrder();

    List<YearChartVo> barOrder(@Param("pairsId") Long pairsId, @Param("symbolNames") String symbolNames);

    @Select("select FROM_UNIXTIME(o.order_time/1000,'%Y%m%d') days,COUNT(o.id) count,SUM(o.order_amount) orderAmount,SUM(o.benefit) benefit FROM biz_order o  LEFT JOIN qa_transaction_category_child c ON o.pairs_id=c.id WHERE o.type=1 and o.order_time BETWEEN #{startTime} and #{endTime} group by days")
    List<YearChartVo> barAllMonth(long startTime, long endTime);

    List<YearChartVo> barMonth(@Param("query") YearChartVo yearChartVo);
}
