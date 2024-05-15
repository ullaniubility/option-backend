package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.qo.AdminOrderQo;
import com.ulla.modules.business.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @Description 后台订单管理
 * @author zhuyongdong
 * @since 2023-03-30 17:08:50
 */
@Api(value = "后台订单管理", tags = {"后台订单管理"})
@RestController
@RequestMapping("/adminOrder")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    final OrderService orderService;

    @ApiOperation("根据条件分页查询订单")
    @GetMapping(value = "/listByPage")
    public ResultMessageVo orderList(AdminOrderQo adminOrderQo) {
        return ResultUtil.data(orderService.adminOrderListByPage(adminOrderQo));
    }

}
