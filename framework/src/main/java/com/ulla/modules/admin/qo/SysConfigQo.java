package com.ulla.modules.admin.qo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@ApiModel(value = "SysConfigQo")
@Data
@ToString
public class SysConfigQo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer page;

    private Integer pageSize;

    private String name;
}
