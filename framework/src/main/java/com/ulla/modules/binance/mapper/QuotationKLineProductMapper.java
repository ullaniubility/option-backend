package com.ulla.modules.binance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.binance.mo.QuotationKLineProductMo;

/**
 * @Description 币安行情
 * @author zhuyongdong
 * @since 2023-02-21 20:59:34
 */
public interface QuotationKLineProductMapper extends BaseMapper<QuotationKLineProductMo> {

    @Select({"SELECT id, stream_time, symbol, k_start_time, k_end_time, close_price, "
        + "open_price, high_price, low_price, turnover_num, turnover_amount, tran_limit, "
        + "close_flag FROM ba_quotation_kline_${klineType} where symbol = #{symbol} and stream_time = #{dataTime}"})
    List<QuotationKLineProductMo> selectKLine(String symbol, Long dataTime, String klineType);

    @Select({"SELECT id, stream_time, symbol, k_start_time, k_end_time, close_price, "
        + "open_price, high_price, low_price, turnover_num, turnover_amount, tran_limit, "
        + "close_flag FROM ba_quotation_kline_${klineType} where symbol = #{symbol} and close_flag = 1 "
        + "and stream_time > #{startDataTime} and stream_time < #{endDataTime} GROUP BY stream_time"})
    List<QuotationKLineProductMo> selectHisList(String symbol, String klineType, Long startDataTime, Long endDataTime);

}
