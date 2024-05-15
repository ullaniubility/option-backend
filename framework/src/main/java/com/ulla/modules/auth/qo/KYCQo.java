package com.ulla.modules.auth.qo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@ApiModel(value = "KYCQo")
@Data
@ToString
public class KYCQo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer page;

    private Integer pageSize;

    // openId
    private String openId;

    // 开始时间
    private Long startTime;

    // 结束时间
    private Long endTime;

    // 国籍
    private String country;

    // 证件号码
    private String certificateNum;

    // 证件类型id
    private Long certificateId;
}
