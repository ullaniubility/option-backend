package com.ulla.modules.business.qo;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HistoryQo {

    @ApiModelProperty("当前页")
    @TableField(exist = false)
    private Integer page = 1;

    @ApiModelProperty("每页大小")
    @TableField(exist = false)
    private Integer limit = 20;

    private Long uid;
}
