package com.ulla.modules.payment.vo;

import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;

import lombok.Data;

@Data
public class TransactionParamerVO extends MoneyPaymentTransactionEntity {

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页显示数量
     */
    private Integer pageSize;

    private String wallectAddress;

}
