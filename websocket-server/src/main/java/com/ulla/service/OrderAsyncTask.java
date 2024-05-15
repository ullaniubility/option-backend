package com.ulla.service;

import java.util.List;

import com.ulla.modules.business.mo.OrderMo;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/3/15 21:54
 */
public interface OrderAsyncTask {

    void quotationInterpose(Long tradingEndTime, List<OrderMo> list);

    void orderCount(OrderMo orderMo);

    void orderRank(String userCode, String symbol);
}
