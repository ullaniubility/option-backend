package com.ulla.modules.binance.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;

@Data
@TableName("qa_exchange_rate")
public class ExchangeRateMo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 二级数据编号
     */
    @TableField("child_id")
    private Long childId;
    /**
     * 二级数据名称
     */
    @TableField("child_name")
    private String childName;
    /**
     * 当前价格（$）
     */
    @TableField("exchange_rate")
    private String exchangeRate;
    /**
     * 是否生效 0.否 1.是
     */
    @TableField("status_flag")
    private Integer statusFlag;

}
