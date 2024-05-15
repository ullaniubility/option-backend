package com.ulla.modules.business.qo;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderQo {

    @ApiModelProperty("当前页")
    @TableField(exist = false)
    private Integer page = 1;

    @ApiModelProperty("每页大小")
    @TableField(exist = false)
    private Integer limit = 20;

    @ApiModelProperty("查询区间")
    @TableField(exist = false)
    private Long beginTime;
    @ApiModelProperty("查询区间")
    @TableField(exist = false)
    private Long endTime;

    /**
     * 交易对
     */
    @TableField("pairs")
    private String pairs;

    /**
     * 涨跌类型 0。买跌 1。买涨
     */
    @TableField("open_close")
    private Integer openClose;

    /**
     * 0没亏没赚零，1亏，2赚，3卖出
     */
    @TableField(exist = false)
    private Integer ProfitLoss;

    /**
     * 所属交易区间id（交易区间默认固定）
     */
    @TableField("trading_range")
    private String tradingRange;

    private Long uid;

    private Long userId;

    @TableField("order_range_id")
    private String orderRangeId;

    private String openId;

}
