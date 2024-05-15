package com.ulla.modules.auth.vo;

import java.io.Serializable;

import com.ulla.modules.auth.mo.UserMo;

import lombok.Data;

@Data
public class UserDetailVo extends UserMo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String wallectAddress;
}
