package com.ulla.modules.admin.qo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 后台操作用户的资金
 * @since 2023/4/11 17:24
 */
@Data
public class UpdateBalanceQo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户uid")
    @NotNull(message = "用户uid不能为空")
    private Long userId;

    @ApiModelProperty("操作资金")
    @NotNull(message = "操作资金不能为空")
    private BigDecimal amount;

    @ApiModelProperty("0 奖金 1 真实资金 3 模拟资金")
    @NotNull(message = "操作的资金类型不能为空")
    private Integer type;

    @ApiModelProperty("操作说明")
    private String remark;

}
