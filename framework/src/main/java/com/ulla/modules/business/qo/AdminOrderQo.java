package com.ulla.modules.business.qo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ulla.common.vo.PageVo;

import cn.hutool.core.text.CharSequenceUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdminOrderQo extends PageVo {

    @ApiModelProperty("用户openId")
    private String openId;

    @ApiModelProperty("用户uid")
    private Long uid;

    @ApiModelProperty("订单编号")
    private String orderCode;

    @ApiModelProperty("资金类型")
    private String pairs;

    @ApiModelProperty("订单状态：0，下单 1，生效 2，结束 3，撤销 4，挂单")
    private Integer status;

    @ApiModelProperty("订单类型（0，虚拟订单 1，正常订单 2，机器人订单）")
    private Integer type;

    @ApiModelProperty("结算查询开始时间")
    private Long tradingBeginTime;

    @ApiModelProperty("结算查询结束时间")
    private Long tradingEndTime;

    @ApiModelProperty("下单查询开始时间")
    private Long orderBeginTime;

    @ApiModelProperty("下单查询结束时间")
    private Long orderEndTime;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (ObjectUtils.isNotEmpty(uid)) {
            queryWrapper.like("uid", uid);
        }
        if (CharSequenceUtil.isNotEmpty(orderCode)) {
            queryWrapper.like("order_code", orderCode);
        }
        if (CharSequenceUtil.isNotEmpty(pairs)) {
            queryWrapper.eq("pairs", pairs);
        }
        if (null != status) {
            queryWrapper.eq("status", status);
        }
        if (null != type) {
            queryWrapper.eq("type", type);
        }
        if (null != tradingBeginTime) {
            queryWrapper.ge("end_time", tradingBeginTime);
        }
        if (null != tradingEndTime) {
            queryWrapper.le("end_time", tradingEndTime);
        }
        if (null != orderBeginTime) {
            queryWrapper.ge("order_time", orderBeginTime);
        }
        if (null != orderEndTime) {
            queryWrapper.le("order_time", orderEndTime);
        }
        return queryWrapper;
    }

}
