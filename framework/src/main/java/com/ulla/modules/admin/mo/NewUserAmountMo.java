package com.ulla.modules.admin.mo;

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
public class NewUserAmountMo extends SysConfigMo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("初始用户模拟余额数")
    private String amount;

    @ApiModelProperty("是否无限 0 否， 1是")
    private Integer infiniteReset;

}
