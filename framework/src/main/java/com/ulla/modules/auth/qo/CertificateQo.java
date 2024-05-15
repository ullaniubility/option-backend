package com.ulla.modules.auth.qo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@ApiModel(value = "CertificateQo")
@Data
@ToString
public class CertificateQo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer page;

    private Integer pageSize;

    // 用户昵称
    private String name;

}
