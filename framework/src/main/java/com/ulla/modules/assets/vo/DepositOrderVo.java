package com.ulla.modules.assets.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ulla.common.serializer.BigDecimalJsonSerializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-11
 */
@Getter
@Setter
public class DepositOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal amount;
    private Long createTime;
    private String uid;
    private String orderNo;

}
