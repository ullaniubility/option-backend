package com.ulla.modules.assets.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ulla.common.serializer.BigDecimalJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 奖金vo
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
public class BonusVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal bonus;
    private Long depositId;
    private Integer firstDepositFlag;
}
