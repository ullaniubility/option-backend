package com.ulla.modules.business.qo;

import java.io.Serializable;

import com.ulla.modules.business.mo.MoneyDepositMo;

import lombok.Data;

/**
 * @author pag
 */
@Data
public class MoneyDepositQo extends MoneyDepositMo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 筛选开始时间
     */
    private Long beginTime;
    /**
     * 筛选结束时间
     */
    private Long endTime;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页显示数量
     */
    private Integer pageSize = 10;

    private String wallectAddress;

}
