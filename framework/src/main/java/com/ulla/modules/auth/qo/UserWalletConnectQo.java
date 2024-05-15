package com.ulla.modules.auth.qo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ulla.modules.auth.mo.UserWalletConnectMo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserWalletConnectQo extends UserWalletConnectMo {
    private Integer page = 1;

    private Integer pageSize = 10;

    @ApiModelProperty("查询区间")
    @TableField(exist = false)
    private Long beginTime;
    @ApiModelProperty("查询区间")
    @TableField(exist = false)
    private Long endTime;
}
