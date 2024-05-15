package com.ulla.controller;

import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.RocketMqConstants;
import com.ulla.modules.assets.service.DepositConfigService;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.service.MoneyPaymentService;
import com.ulla.modules.payment.service.MoneyPaymentTransactionService;
import com.ulla.modules.payment.vo.FigureCurrencyParamerVO;
import com.ulla.modules.payment.vo.UpdatePayStatusVo;
import com.ulla.task.PaymentJobHandler;
import com.ulla.util.MqUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description {入金相关功能controller}
 * @author {michael}
 * @since {2023-2-25}
 */
@Api(value = "入金相关功能信息", tags = {"入金相关功能信息"})
@RestController
@RequestMapping("/moneypayment")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MoneyPaymentController {

    final MoneyPaymentService moneyPaymentService;

    final MoneyPaymentTransactionService moneyPaymentTransactionService;

    final PaymentJobHandler paymentJobHandler;

    final DepositConfigService depositConfigService;

    /**
     * 创建数字币交易订单
     * 
     * @param vo
     *            参数VO
     * @return
     */
    @ApiOperation("创建数字币交易订单")
    @PostMapping(value = "/createFigureCurrency")
    public ResultMessageVo createFigureCurrency(@Valid @RequestBody FigureCurrencyParamerVO vo) {
        return moneyPaymentService.createFigureCurrency(vo);
    }

    /**
     * 变更订单状态
     * 
     * @param vo
     * @return
     */
    @ApiOperation("变更订单状态法")
    @PostMapping(value = "/updateLegalCurrency")
    public ResultMessageVo updateLegalCurrency(@RequestBody UpdatePayStatusVo vo) {
        return moneyPaymentService.updateLegalCurrency(vo);
    }

    /**
     * 数字货币变更接口
     * 
     * @param map
     * @return
     */
    @PostMapping("/figureCurrencyRecharge")
    public ResultMessageVo figureCurrencyRecharge(@RequestBody Map<String, Object> map) {
        try {
            ResultMessageVo messageVo = moneyPaymentService.figureCurrencyRecharge(map);
            log.info(messageVo.toString());
            if (messageVo.isSuccess()) {
                Message sendMsg = new Message(RocketMqConstants.CHARGE_MSG,
                    JSONObject.toJSONString(messageVo.getResult()).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = Objects.requireNonNull(MqUtil.getProducer("ulla")).send(sendMsg);
                log.info(sendResult.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtil.success();

    }

    /**
     * 处理失效订单
     * 
     * @return
     */
    @ApiOperation("处理失效订单")
    @GetMapping(value = "/processInvalidOrders")
    public void processInvalidOrders() {
        paymentJobHandler.processInvalidOrders();
    }

    /**
     * 订单状态完成后，补充奖励金额以及修改订单状态为已完成
     * 
     * @param vo
     *            补充参数VO
     * @return
     */
    @ApiOperation("订单状态完成后，补充奖励金额以及修改订单状态为已完成")
    @PostMapping(value = "/updateRewardAmount")
    public ResultMessageVo updateRewardAmount(@RequestBody MoneyPaymentTransactionEntity vo) {
        return moneyPaymentService.updateRewardAmount(vo);
    }

    /**
     * 查询入金订单是否充值成功
     * 
     * @return
     */
    @GetMapping(value = "/getDepositAmount")
    public ResultMessageVo getDepositAmount() {
        return moneyPaymentTransactionService.getDepositAmount(UserUtil.getOpenId());
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
}
