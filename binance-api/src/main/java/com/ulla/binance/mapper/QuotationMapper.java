package com.ulla.binance.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.binance.mo.QuotationKLineProductMo;
import com.ulla.binance.mo.QuotationProductMo;

/**
 * @author zhuyongdong
 * @Description 币安行情
 * @since 2023-02-21 20:59:34
 */
public interface QuotationMapper extends BaseMapper<QuotationProductMo> {

    @Insert({
        "insert into ba_quotation_kline_${symbol}(stream_time, symbol, k_start_time, k_end_time, close_price, open_price, "
            + "high_price, low_price, turnover_num, turnover_amount, tran_limit, close_flag, "
            + "uuid, trading_range_id, order_range_id, order_start_time, order_end_time, trading_start_time, trading_end_time) "
            + "values( #{mo.streamTime}, #{mo.symbol}, #{mo.kStartTime}, #{mo.kEndTime}, "
            + "#{mo.closePrice}, #{mo.openPrice}, #{mo.highPrice}, #{mo.lowPrice}, "
            + "#{mo.turnoverNum}, #{mo.turnoverAmount}, #{mo.tranLimit}, #{mo.closeFlag}, "
            + "#{mo.uuid}, #{mo.tradingRangeId}, #{mo.orderRangeId}, #{mo.orderStartTime}, "
            + "#{mo.orderEndTime}, #{mo.tradingStartTime}, #{mo.tradingEndTime})"})
    void insertOneSeconds(String symbol, QuotationProductMo mo);

    @Select("SELECT COUNT(1) FROM ba_quotation_kline_${symbol} WHERE stream_time = #{beforeTime}")
    int selectCount(String symbol, Long beforeTime);

    @Select("SELECT COUNT(1) FROM ba_quotation_kline_5s WHERE symbol = #{symbol} AND stream_time = #{beforeTime}")
    int select5SCount(String symbol, Long beforeTime);

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

    @Select("SELECT COUNT(1) FROM ba_quotation_kline_${symbol} WHERE stream_time <= #{beforeTime} AND stream_time >= #{startTime}")
    int countNumber(String symbol, Long beforeTime, Long startTime);
}
