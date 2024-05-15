package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.qo.AdminOrderQo;
import com.ulla.modules.business.qo.OrderHistoryQo;
import com.ulla.modules.business.qo.OrderQo;
import com.ulla.modules.business.vo.MarketAnalysisVo;
import com.ulla.modules.business.vo.OrderSearchVo;
import com.ulla.modules.business.vo.OrderVo;

/**
 * @author {clj}
 * @Description {订单service}
 * @since {2023-2-21}
 */
public interface OrderService extends IService<OrderMo> {

    ResultMessageVo order(OrderVo orderVo, Long uid);

    ResultMessageVo placeOrder(OrderMo order);

    MarketAnalysisVo getByUid(Long uid);

    ResultMessageVo deleteOrder(Long orderId);

    IPage<OrderSearchVo> simulationOrder(Page<OrderQo> page, OrderQo qo);

    ResultMessageVo closeOrder(Long orderId);

    IPage<OrderMo> adminOrderListByPage(AdminOrderQo adminOrderQo);

    ResultMessageVo listByPage(Integer pageNumber, Integer pageSize, Integer dataType, Long uid);

    ResultMessageVo page(OrderHistoryQo orderHistoryQo, Long uid);

    ResultMessageVo getDetailById(String orderId, Long uid);
}
