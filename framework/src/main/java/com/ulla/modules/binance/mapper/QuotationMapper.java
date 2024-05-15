package com.ulla.modules.binance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.binance.mo.QuotationKLineProductMo;
import com.ulla.modules.binance.mo.QuotationMo;
import com.ulla.modules.binance.mo.QuotationProductMo;

/**
 * @author zhuyongdong
 * @Description 币安行情
 * @since 2023-02-21 20:59:34
 */
public interface QuotationMapper extends BaseMapper<QuotationProductMo> {

    @Select({
        "SELECT id,stream_time,symbol,k_start_time,k_end_time,close_price,open_price,high_price,low_price,turnover_num,turnover_amount, "
            + "tran_limit,close_flag,uuid,trading_range_id,order_range_id,order_start_time,order_end_time,trading_start_time,trading_end_time "
            + "from ba_quotation_kline_maticusdt where stream_time = #{streamTime}"})
    List<QuotationProductMo> selectMaticusdt(Long streamTime);

    @Select({
        "SELECT id,stream_time,symbol,k_start_time,k_end_time,close_price,open_price,high_price,low_price,turnover_num,turnover_amount, "
            + "tran_limit,close_flag,uuid,trading_range_id,order_range_id,order_start_time,order_end_time,trading_start_time,trading_end_time "
            + "from ba_quotation_kline_btcusdt where stream_time = #{streamTime}"})
    List<QuotationProductMo> selectBtcusdt(Long streamTime);

    @Select({"SELECT id, stream_time, symbol, close_price, open_price, high_price, low_price, "
        + "turnover_num, turnover_amount, uuid, trading_range_id, order_range_id, "
        + "order_start_time, order_end_time, trading_start_time, trading_end_time from ba_quotation_kline_${symbol} "
        + "where stream_time > #{startDataTime} and stream_time < #{endDataTime}"})
    List<QuotationProductMo> selectHisList(String symbol, Long startDataTime, Long endDataTime);

    @Select({"SELECT id, stream_time, symbol, close_price, open_price, high_price, low_price, "
        + "turnover_num, turnover_amount, uuid, trading_range_id, order_range_id, "
        + "order_start_time, order_end_time, trading_start_time, trading_end_time from ba_quotation_kline_${symbol} "
        + "where stream_time > #{startDataTime} and stream_time < #{endDataTime}"})
    List<QuotationMo> selectByRange(String symbol, Long startDataTime, Long endDataTime);

    @Select({"SELECT id, stream_time, symbol, close_price, open_price, high_price, low_price, "
        + "turnover_num, turnover_amount, uuid, trading_range_id, order_range_id, "
        + "order_start_time, order_end_time, trading_start_time, trading_end_time from ba_quotation_kline_${symbol} "
        + "where stream_time = #{streamTime}  ORDER BY stream_time DESC LIMIT 1"})
    QuotationProductMo selectQuotationByStreamTime(String symbol, Long streamTime);

    @Select({
        "SELECT id,stream_time,symbol,k_start_time,k_end_time,close_price,open_price,high_price,low_price,turnover_num,turnover_amount, "
            + "tran_limit,close_flag,uuid,trading_range_id,order_range_id,order_start_time,order_end_time,trading_start_time,trading_end_time "
            + "from ba_quotation_kline_${symbol} where stream_time = #{streamTime} LIMIT 1"})
    QuotationProductMo selectMoByStreamTime(String symbol, Long streamTime);

    @Select({"SELECT id, stream_time, symbol, close_price, open_price, high_price, low_price, "
        + "turnover_num, turnover_amount, uuid, trading_range_id, order_range_id, "
        + "order_start_time, order_end_time, trading_start_time, trading_end_time from ba_quotation_kline_${symbol} "
        + "where stream_time > #{startDataTime} and stream_time < #{endDataTime}  ORDER BY stream_time"})
    List<QuotationProductMo> selectByTradingTime(String symbol, Long startDataTime, Long endDataTime);

    @Insert({
        "insert into ba_quotation_kline_5s(stream_time, symbol, k_start_time, k_end_time, close_price, open_price, "
            + "high_price, low_price, turnover_num, turnover_amount, tran_limit, close_flag) "
            + "values( #{streamTime}, #{symbol}, #{kStartTime}, #{kEndTime}, "
            + "#{closePrice}, #{openPrice}, #{highPrice}, #{lowPrice}, "
            + "#{turnoverNum}, #{turnoverAmount}, #{tranLimit}, #{closeFlag})"})
    void insertFiveSeconds(QuotationKLineProductMo mo);

    @Insert({
        "insert into ba_quotation_kline_${intervalType}(stream_time, symbol, k_start_time, k_end_time, close_price, open_price, "
            + "high_price, low_price, turnover_num, turnover_amount, tran_limit, close_flag) "
            + "values( #{mo.streamTime}, #{mo.symbol}, #{mo.kStartTime}, #{mo.kEndTime}, "
            + "#{mo.closePrice}, #{mo.openPrice}, #{mo.highPrice}, #{mo.lowPrice}, "
            + "#{mo.turnoverNum}, #{mo.turnoverAmount}, #{mo.tranLimit}, #{mo.closeFlag})"})
    void binanceKLineByInterval(QuotationKLineProductMo mo, String intervalType);
}
