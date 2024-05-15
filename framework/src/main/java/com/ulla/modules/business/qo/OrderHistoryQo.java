package com.ulla.modules.business.qo;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.PageVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 历史记录查询模型
 * @author zhuyongdong
 * @since 2023-04-01 10:33:17
 */
@Data
public class OrderHistoryQo extends PageVo {

    @ApiModelProperty("订单开始日期")
    private Long beginTime;

    @ApiModelProperty("订单结束日期")
    private Long endTime;

    @ApiModelProperty("交易对")
    private String pairs;

    @ApiModelProperty("涨跌类型 0。买跌 1。买涨")
    private Integer openClose;

    @ApiModelProperty("盈亏 0亏 1赚 2没亏没赚零 3卖出")
    private Integer ifProfit;

    @ApiModelProperty(value = "用户编号", hidden = true)
    private Long uid;

    @NotNull(message = "订单类型不能为空")
    @ApiModelProperty("订单类型 0虚拟订单 1正常订单")
    private Integer type;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(pairs)) {
            queryWrapper.eq("pairs", pairs);
        }
        if (null != openClose) {
            queryWrapper.eq("open_close", openClose);
        }
        if (null != ifProfit) {
            queryWrapper.eq("if_profit", ifProfit);
        }
        if (null != beginTime) {
            queryWrapper.ge("order_time", beginTime);
        }
        if (null != endTime) {
            queryWrapper.le("order_time", endTime);
        }
        queryWrapper.eq("type", type);
        queryWrapper.eq("uid", uid);
        queryWrapper.and(wrapper -> wrapper.eq("status", 2).or().eq("status", 3));
        return queryWrapper;
    }

}
