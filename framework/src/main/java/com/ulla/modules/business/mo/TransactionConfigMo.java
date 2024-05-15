package com.ulla.modules.business.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("qa_transaction_config")
@ApiModel(value = "平仓损耗配置", description = "平仓损耗配置")
public class TransactionConfigMo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("会员交易下单立即撤单平仓损耗比例")
    @TableField("loss_ratio")
    private Integer lossRatio;

    @ApiModelProperty("会员交易撤单平仓最少可撤比例")
    @TableField("withdrawal")
    private Integer withdrawal;

}
