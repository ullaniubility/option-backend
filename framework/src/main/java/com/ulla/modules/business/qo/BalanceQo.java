package com.ulla.modules.business.qo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BalanceQo {
    @ApiModelProperty("当前页")
    @TableField(exist = false)
    private Integer page = 1;

    @ApiModelProperty("每页大小")
    @TableField(exist = false)
    private Integer limit = 20;

    private String uid;

    private Long beginTime;

    private Long endTime;

    private String startTime;

    private String overTime;

    @ApiModelProperty("0：待付款\n" + "1：已付款\n" + "2：已完成\n" + "3：已失效\n" + "4：已失败")
    private Integer orderStatus;

    @ApiModelProperty("业务类型：1入金2提现3交易4奖励")
    private Integer businessType;

    @ApiModelProperty("订单号")
    private String logNo;

    @ApiModelProperty("用户账号")
    private String mail;

    @ApiModelProperty("变动金额")
    private BigDecimal amount;

    /**
     * 平台盈亏：1盈利2亏损
     */
    private Integer profitLoss;

    /**
     * 1是本天，2是本周，3是本月，4是本季度，5是本年
     */
    private Integer typeTime;

}
