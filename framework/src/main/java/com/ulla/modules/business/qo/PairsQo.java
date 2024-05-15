package com.ulla.modules.business.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 交易一级大类
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
@Data
public class PairsQo {

    @ApiModelProperty("编号")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("父类编号")
    private Long parentId;

}
