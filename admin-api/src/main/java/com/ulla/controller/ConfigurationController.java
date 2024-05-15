package com.ulla.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ulla.common.controller.UserBaseController;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.ActiveMo;
import com.ulla.modules.assets.mo.DepositConfigMo;
import com.ulla.modules.assets.service.*;
import com.ulla.modules.assets.vo.ActiveParameterVO;
import com.ulla.modules.assets.vo.AddressParameterVO;
import com.ulla.modules.assets.vo.BalanceLogParameterVO;
import com.ulla.modules.payment.service.MoneyPaymentTransactionService;
import com.ulla.modules.payment.vo.MoneyPaymentTransactionVO;
import com.ulla.modules.payment.vo.TransactionParamerVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description {入金配置相关功能}
 * @author {michael}
 * @since {2023-3-21}
 */
@Api(value = "入金配置相关功能", tags = {"入金配置相关功能"})
@RestController
@RequestMapping("/config")
public class ConfigurationController extends UserBaseController {

    @Autowired
    private DepositConfigService depositConfigService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private BalanceLogService balanceLogService;

    @Autowired
    private ActiveService activeService;

    @Autowired
    private DepositConfigHistoryService depositConfigHistoryService;

    @Autowired
    private MoneyPaymentTransactionService moneyPaymentTransactionService;

    /**
     * 入金配置 录入接口
     * 
     * @param mo
     *            参数VO
     * @return
     */
    @ApiOperation("入金配置 录入接口")
    @PostMapping(value = "/saveDepositConfig")
    public ResultMessageVo saveDepositConfig(@RequestBody DepositConfigMo mo) {
        return depositConfigService.saveDepositConfig(mo);
    }

    /**
     * 入金配置 修改接口
     * 
     * @param mo
     *            参数VO
     * @return
     */
    @ApiOperation("入金配置 修改接口")
    @PostMapping(value = "/updateDepositConfig")
    public ResultMessageVo updateDepositConfig(@RequestBody DepositConfigMo mo) {
        return depositConfigService.updateDepositConfig(mo);
    }

    /**
     * 入金配置 - 获取入金配置列表
     * 
     * @return
     */
    @ApiOperation("入金配置 - 获取入金配置列表")
    @GetMapping(value = "/getDepositConfigList")
    public ResultMessageVo getDepositConfigList(PageVo page) {
        return depositConfigService.getDepositConfigList(page);
    }

    /**
     * 入金配置 - 获取所有正常状态的入金配置
     * 
     * @return
     */
    @ApiOperation("入金配置 - 获取所有正常状态的入金配置")
    @GetMapping(value = "/getNormalDepositConfigList")
    public ResultMessageVo getNormalDepositConfigList() {
        return depositConfigService.getNormalDepositConfigList();
    }

    /**
     * 入金配置 - 根据配置Id查询配置详情
     * 
     * @param id
     *            入金配置Id
     * @return
     */
    @ApiOperation("入金配置 - 根据配置Id查询配置详情")
    @GetMapping(value = "/getDepositConfigById")
    public ResultMessageVo getDepositConfigById(@RequestParam Long id) {
        return depositConfigService.getDepositConfigById(id);
    }

    /**
     * 钱包地址池列表查询
     * 
     * @param vo
     *            参数VO
     * @return
     */
    @ApiOperation("钱包地址池列表查询接口")
    @PostMapping(value = "/addressListByParamer")
    public ResultMessageVo addressListByParamer(@RequestBody AddressParameterVO vo) {
        return addressService.addressListByParamer(vo);
    }

    /**
     * 资金流水表列表查询
     * 
     * @param vo
     *            列表查询参数
     * @return
     */
    @ApiOperation("资金流水表列表查询接口")
    @PostMapping(value = "/balanceLogListByParamer")
    public ResultMessageVo balanceLogListByParamer(@RequestBody BalanceLogParameterVO vo) {
        return balanceLogService.balanceLogListByParamer(vo);
    }

    /**
     * 促销活动列表查询
     * 
     * @param vo
     *            列表查询参数
     * @return
     */
    @ApiOperation("促销活动列表查询")
    @PostMapping(value = "/activeListByParamer")
    public ResultMessageVo activeListByParamer(@RequestBody ActiveParameterVO vo) {
        ResultMessageVo resultMessageVo = activeService.activeListByParamer(vo);
        if (ObjectUtils.isEmpty(resultMessageVo)) {
            return null;
        }
        return resultMessageVo;
    }

    /**
     * 促销活动列表查询
     *
     * list查询
     * 
     * @return
     */
    @ApiOperation("促销活动list查询")
    @GetMapping(value = "/activeList")
    public ResultMessageVo activeList() {
        return activeService.activeList();
    }

    /**
     * 促销码列表查询
     * 
     * @param vo
     *            列表查询参数
     * @return
     */
    @ApiOperation("促销码列表查询")
    @PostMapping(value = "/activeCouponListByParamer")
    public ResultMessageVo activeCouponListByParamer(@RequestBody ActiveParameterVO vo) {
        return activeService.activeCouponListByParamer(vo);
    }

    // @GetMapping(value = "/getDepositConfigById")
    // public ResultMessageVo getDepositConfigById(@RequestParam("id") @Min(1) Integer id) {
    // return ResultUtil.data(depositConfigHistoryService.getById(id));
    // }

    /**
     * 入金订单列表查询
     *
     * @param vo
     *            列表查询参数
     * @return
     */
    @ApiOperation("入金订单列表查询")
    @PostMapping(value = "/transactionListByParamer")
    public ResultMessageVo transactionListByParamer(@RequestBody TransactionParamerVO vo) {
        ResultMessageVo resultMessageVo = moneyPaymentTransactionService.transactionListByParamer(vo);
        if (ObjectUtils.isEmpty(resultMessageVo)) {
            return null;
        }
        return resultMessageVo;
    }

    /**
     * 入金订单详情查询
     * 
     * @param orderId
     *            订单Id
     * @return
     */
    @ApiOperation("入金订单详情查询")
    @GetMapping(value = "/getTransactionInfoById")
    public ResultMessageVo<MoneyPaymentTransactionVO> getTransactionInfoById(@RequestParam String orderId) {
        return moneyPaymentTransactionService.getWallectByOrderId(orderId);
    }

    /**
     * 订单失效或者失败时 一键补发操作
     * 
     * @param orderId
     *            订单Id
     * @return
     */
    @ApiOperation("订单失效或者失败时 一键补发操作")
    @GetMapping(value = "/supplyAgain")
    public ResultMessageVo supplyAgain(@RequestParam String orderId, @RequestParam String reasonInfo) {
        return moneyPaymentTransactionService.supplyAgain(orderId, reasonInfo);
    }

    /**
     * 促销活动管理 - 促销活动修改接口
     * 
     * @param mo
     *            促销活动修改参数
     * @return
     */
    @ApiOperation("促销活动管理 - 促销活动修改接口")
    @PostMapping(value = "/updateActive")
    public ResultMessageVo updateActive(@RequestBody ActiveMo mo) {
        return activeService.updateActive(mo);
    }

    /**
     * 促销活动管理 - 促销活动详情获取接口
     * 
     * @param id
     *            促销活动Id
     * @return
     */
    @ApiOperation("促销活动管理 - 促销活动详情获取接口")
    @PostMapping(value = "/getActiveInfoById")
    public ResultMessageVo getActiveInfoById(@RequestParam Long id) {
        return activeService.getActiveInfoById(id);
    }

}
