package com.ulla.modules.business.mo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 提现配置
 * @since 2023/4/14 13:28
 */
@Data
@ApiModel(value = "平仓损耗配置", description = "平仓损耗配置")
public class WithdrawalMo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("最大提现额度对应交易额度的百分比")
    private Integer withdrawalAmountPercent;

    @ApiModelProperty("最小提现额度")
    private Integer lowAmount;

}
