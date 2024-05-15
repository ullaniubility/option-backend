package com.ulla.modules.binance.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 公司每日盈亏
 */
@Data
@TableName("qa_profit_loss")
public class ProfitLossMo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    @TableId("id")
    private Long id;
    /**
     * 盈利
     */
    @TableField("profit")
    private String profit;
    /**
     * 亏损
     */
    @TableField("loss")
    private String loss;

}
