package com.ulla.modules.assets.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ulla.common.serializer.BigDecimalJsonSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class WithdrawOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal amount;
    private Long userId;
    @NonNull
    @Length(min = 1)
    private String uid;
    private String orderNo;
    private Integer state;
    private Long auditTime;
    private Long auditBy;
    private String createBy;
    private Long createTime;
    private Boolean deleteFlag;
    private String updateBy;
    private Long updateTime;


}
