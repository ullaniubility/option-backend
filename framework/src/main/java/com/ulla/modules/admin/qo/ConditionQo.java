package com.ulla.modules.admin.qo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConditionQo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String net;

    private String symbol;

    private String splicing;
}
