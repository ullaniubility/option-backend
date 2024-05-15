package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.qo.OrderHistoryQo;
import com.ulla.modules.business.qo.OrderQo;
import com.ulla.modules.business.service.OrderService;
import com.ulla.modules.business.vo.OrderHistoryResultVo;
import com.ulla.modules.business.vo.OrderSearchVo;
import com.ulla.modules.business.vo.OrderVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description {用户controller}
 * @author {clj}
 * @since {2023-2-13}
 */
@Slf4j
@Api(value = "订单", tags = {"订单"})
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    final OrderService orderService;

    @ApiOperation("下单")
    @PostMapping(value = "/order")
    public ResultMessageVo order(@RequestBody OrderVo orderVo) {
        return orderService.order(orderVo, UserUtil.getUid());
    }

    @ApiOperation("撤销订单")
    @GetMapping(value = "/closeOrder")
    public ResultMessageVo closeOrder(Long orderId) {
        return orderService.closeOrder(orderId);
    }

    @ApiOperation(value = "条件筛选交易记录", notes = "条件筛选交易记录")
    @GetMapping("/page")
    public ResultMessageVo<OrderHistoryResultVo> page(@Validated OrderHistoryQo orderHistoryQo) {
        log.info(orderHistoryQo.toString());
        log.info(UserUtil.getUid().toString());
        return orderService.page(orderHistoryQo, UserUtil.getUid());
    }

    @ApiOperation(value = "条件筛选交易记录", notes = "条件筛选交易记录")
    @GetMapping("/getDetailById")
    public ResultMessageVo getDetailById(String orderIds) {
        return orderService.getDetailById(orderIds, UserUtil.getUid());
    }

    /**
     * @Description 交易里的最近交易
     * @Param pageNo 页码
     * @Param pageSize 条数
     * @Param dataType 0 模拟 1真实
     * @author zhuyongdong
     * @since 2023-03-30 20:51:40
     */
    @ApiOperation(value = "交易里的最近交易", notes = "交易里的最近交易")
    @GetMapping("/pageRecent")
    public ResultMessageVo<IPage<OrderMo>> pageRecent(Integer pageNumber, Integer pageSize, Integer dataType) {
        return orderService.listByPage(pageNumber, pageSize, dataType, UserUtil.getUid());
    }

    /**
     * 用户模拟交易订单
     */
    @PostMapping("/simulationPage")
    @ApiOperation(value = "交易记录分页", notes = "交易记录")
    public ResultMessageVo<IPage<OrderSearchVo>> simulationOrder(@RequestBody OrderQo qo) {
        Page<OrderQo> page = new Page<>(qo.getPage(), qo.getLimit());
        return ResultUtil.data(orderService.simulationOrder(page, qo));
    }

}
