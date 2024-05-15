package com.ulla.modules.assets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户钱包地址表
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Data
public class AddressVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String net;
    private String address;
}
