package com.ulla.modules.assets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 地址池列表查询参数VO
 * </p>
 *
 * @author michael
 * @since 2023-03-22
 */
@Data
public class AddressParameterVO implements Serializable {

    /**
     * 状态 0-未绑定 1，已绑定 2，已失效
     */
    private Integer state;
    /**
     * 绑定的用户id
     */
    private String userId;
    /**
     * 公链
     */
    private String net;
    /**
     * 地址
     */
    private String address;

    /**
     * 当前页
     */
    private Integer page;
    /**
     * 每页展示数量
     */
    private Integer pageSize;

}
