package com.ulla.modules.payment.service.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.IdUtils;
import com.ulla.common.utils.PaymentUtil;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.common.vo.exception.ServiceException;
import com.ulla.constant.NumberConstant;
import com.ulla.constant.PaymentConstant;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.mapper.AddressMapper;
import com.ulla.modules.assets.service.ActiveCouponService;
import com.ulla.modules.assets.service.AddressService;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.service.DepositConfigHistoryService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.assets.vo.BonusVo;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.auth.mo.UserLevelMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.payment.entity.MoneyPaymentChannelEntity;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.entity.MoneyTestChannelEntity;
import com.ulla.modules.payment.entity.SysRateEntity;
import com.ulla.modules.payment.mapper.*;
import com.ulla.modules.payment.service.MoneyPaymentService;
import com.ulla.modules.payment.vo.CallResponseVO;
import com.ulla.modules.payment.vo.FigureCurrencyParamerVO;
import com.ulla.modules.payment.vo.UpdatePayStatusVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

/**
 * 入金功能
 *
 * @author michael
 * @email 123456789@qq.com
 * @date 2023-02-25 14:51:23
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MoneyPaymentServiceImpl implements MoneyPaymentService {

    final MoneyPaymentTransactionMapper moneyPaymentTransactionMapper;

    final PaymentUtil paymentUtil;

    final AddressMapper addressMapper;

    final DepositConfigHistoryService depositConfigHistoryService;

    final SysRateMapper sysRateMapper;

    final ActiveCouponService activeCouponService;

    final AddressService addressService;

    final BalanceService balanceService;

    final UserMapper userMapper;

    final PaymentCurrencyMapper paymentCurrencyMapper;

    final MoneyPaymentChannelMapper channelMapper;

    final MoneyTestChannelMapper testChannelMapper;

    final Cache cache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo createFigureCurrency(FigureCurrencyParamerVO vo) {
        MoneyPaymentTransactionEntity entity = new MoneyPaymentTransactionEntity();
        BeanUtils.copyProperties(vo, entity);
        entity.setUid(UserUtil.getUid());
        entity.setOpenId(UserUtil.getOpenId());

        MoneyPaymentChannelEntity channelEntity = new MoneyPaymentChannelEntity();
        MoneyTestChannelEntity testChannel = testChannelMapper.selectById(1l);
        if (null != testChannel && testChannel.getOffFlag().intValue() == 1) {
            BeanUtils.copyProperties(testChannel, channelEntity);
        } else {
            // 查询地址
            LambdaQueryWrapper<MoneyPaymentChannelEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MoneyPaymentChannelEntity::getAddress, entity.getAddress());
            wrapper.eq(MoneyPaymentChannelEntity::getNet, entity.getNet());
            channelEntity = channelMapper.selectOne(wrapper);
        }
        if (Objects.isNull(channelEntity)) {
            throw new ServiceException(ResultCodeEnums.PAY_CASHIER_ILLEGAL);
        }

        entity.setContractAddress(channelEntity.getContractAddress());
        // 收币地址(小写)
        entity.setAddressLower(channelEntity.getAddress().toLowerCase());
        entity.setOrderId(IdUtils.get32SimpleUUID());
        entity.setOrderStatus(PaymentConstant.outstanding_payment);
        // 奖励配置Id为null，用户选择不要奖励,有值，查询奖励信息
        if (ObjectUtils.isNotEmpty(entity.getRewardCode())) {
            // 需要调用资金管理的接口，根据页面选择金额，获取奖励金额和奖励id
            BonusVo bonusVo = depositConfigHistoryService
                .getDepositBonus(Integer.parseInt(entity.getEstimatedDepositAmount().toString()), entity.getUid(),
                    System.currentTimeMillis())
                .getResult();
            if (Objects.isNull(bonusVo)) {
                throw new ServiceException(ResultCodeEnums.REWARD_CONFIG_NOT_EXIST);
            }
            if (!entity.getRewardCode().equals(bonusVo.getDepositId().toString())) {
                throw new ServiceException(ResultCodeEnums.REWARD_CONFIG_DO_NOT_MATE);
            }
            entity.setRewardCode(bonusVo.getDepositId().toString());
            entity.setRewardAmount(bonusVo.getBonus());
            entity.setIsFirstOrder(bonusVo.getFirstDepositFlag());
        }
        // 需要调用资金管理的接口，把订单的促销码，奖励id传入。得到奖励金额和促销金额，赋值更新当前对象
        if (entity.getIsUsePreferential().equals(1)) {
            activeCouponService.couponBindOrder(entity);
        }
        // 现在只有USDT
        entity.setCurrencyPrice(new BigDecimal("1"));
        entity.setCreateTime(System.currentTimeMillis());
        entity.setUpdateTime(System.currentTimeMillis());

        if (moneyPaymentTransactionMapper.insert(entity) == 0) {
            throw new ServiceException(ResultCodeEnums.ORDER_NET_ERROR);
        }
        return ResultUtil.data(entity.getId());
    }

    @Override
    public ResultMessageVo updateLegalCurrency(UpdatePayStatusVo vo) {
        MoneyPaymentTransactionEntity transaction = moneyPaymentTransactionMapper.selectById(vo.getId());
        transaction.setTransactionHash(vo.getTxHash());
        if (optionAddress(transaction.getNet(), transaction.getAddress())) {
            transaction.setOrderStatus(PaymentConstant.completed_payment);
        } else {
            transaction.setOrderStatus(PaymentConstant.completed_payment_pre);
        }
        moneyPaymentTransactionMapper.updateById(transaction);
        return ResultUtil.success();
    }

    public boolean optionAddress(String net, String address) {
        String url = "http://8.210.115.185:7112/optionAddress/addAddress";
        OkHttpClient mOkHttpClient = new OkHttpClient();
        List<JSONObject> mList = new ArrayList<>();
        JSONObject obj = new JSONObject();
        obj.put("net", net);
        obj.put("address", address);
        mList.add(obj);
        MediaType type = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(type, mList.toString());
        Request request = new Request.Builder().url(url).post(requestBody).build();

        Call call = mOkHttpClient.newCall(request);
        // 返回请求结果
        try (Response response = call.execute()) {
            return response.code() == ResultCodeEnums.SUCCESS.code().intValue();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * isSlipPoint 为true 偏差滑点在百分之 10以内 为false 偏差滑点在百分之10以外 计算传给第三方的交易数和公司实际收到的数量值的偏差值是否在10%以内
     * 回溯币种数量，如果第三方支付平台变更了支付币种，需要将价值回溯至美元价值
     * 举例：传给第三方支付平台美元，用户在第三方支付平台使用英镑支付，那么返回的币种数量，以及币种价格对应的就是用户支付的货币价格(即英镑)，价值=币数量*价格*美元汇率
     *
     * @param actualPaymentAmount
     *            传递给第三方支付平台价值（美元）
     * @param vo
     *            回调方法参数VO
     * @return
     */
    public Boolean getSlipPoint(BigDecimal actualPaymentAmount, CallResponseVO vo) {
        BigDecimal tokenAmount = new BigDecimal(vo.getTokenAmount()); // 公司到账币 - 对应币数量（法币支付得到数字币情况）
        BigDecimal conversionPrice = vo.getConversionPrice();// 币种对应的价格
        String currency = vo.getCurrency().toUpperCase();// 用户在第三方支付平台支付的货币单位

        BigDecimal currencyValue = tokenAmount.multiply(conversionPrice);
        // 回调的货币单位不是美元，说明用户在第三方支付平台更改了支付货币
        if (!PaymentConstant.USD.equals(currency)) {
            SysRateEntity entity = sysRateMapper.getRateByUnit(currency);// 获取用户实际支付的货币单位以及美元汇率
            BigDecimal rate = entity.getRate() == null ? new BigDecimal("0") : entity.getRate();
            currencyValue = currencyValue.multiply(rate);// 计算出对应的美元价值
        }
        // 计算传递给第三方支付平台的钱和公司实际收到的钱偏差值是否在10%
        Boolean isSlipPoint = paymentUtil.isSlipPoint(actualPaymentAmount, currencyValue);
        return isSlipPoint;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo figureCurrencyRecharge(Map<String, Object> map) {
        // 交易hash
        String txHash = map.get("txHash").toString();
        String net = map.get("net").toString();
        String symbol = map.get("symbol").toString();
        // 金额
        String amont = map.get("amont").toString();
        BigDecimal amontBig = new BigDecimal(amont);
        // 精度, 计算真实的金额
        String accuracy = map.get("accuracy").toString();
        BigDecimal accuracyBig = new BigDecimal("10");
        accuracyBig = accuracyBig.pow(new Integer(accuracy));
        amontBig = amontBig.divide(accuracyBig, 8, BigDecimal.ROUND_DOWN).stripTrailingZeros();

        // 地址
        String toAddr = map.get("toAddr").toString();
        // 1 主币转账
        // 2 ERC20 类型代币转账
        // 3 NFT 类型代币转账
        // 4 dApp合约调用
        String txType = map.get("txType").toString();
        // 合约地址
        String contract = map.get("contract").toString();
        if (!Objects.equals(net, "ETH") && !Objects.equals(net, "TRX")) {
            log.error("figureCurrencyRecharge主链信息不对,hash:{},toAddr:{},net:{}", txHash, toAddr, net);
            // 不是eth和trx主链的直接返回失败
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
        // 状态
        String status = map.get("status").toString();
        if (!Objects.equals(txType, "2")) {
            // 现在入金只有代币usdt，txType不等于2的都无需处理
            log.error("figureCurrencyRecharge交易类型错误,hash:{},toAddr:{},net:{}", txHash, toAddr, net);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }

        LambdaQueryWrapper<MoneyPaymentChannelEntity> channelQueryWrapper = new LambdaQueryWrapper<>();
        channelQueryWrapper.eq(MoneyPaymentChannelEntity::getNet, net);
        channelQueryWrapper.eq(MoneyPaymentChannelEntity::getAddress, toAddr);
        channelQueryWrapper.eq(MoneyPaymentChannelEntity::getContractAddress, contract);
        long count = channelMapper.selectCount(channelQueryWrapper);

        if (count == 0) {
            // 非法充值
            log.error("figureCurrencyRecharge非法充值,hash:{},toAddr:{},net:{}", txHash, toAddr, net);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }

        LambdaQueryWrapper<MoneyPaymentTransactionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MoneyPaymentTransactionEntity::getTransactionHash, txHash);
        queryWrapper.eq(MoneyPaymentTransactionEntity::getOrderStatus, PaymentConstant.accomplish);
        if (moneyPaymentTransactionMapper.selectCount(queryWrapper) > 0) {
            log.info("figureCurrencyRecharge已有hash已处理，现无需处理,hash:{},toAddr:{},net:{}", txHash, toAddr, net);
            // 已经有相关的hash数据，无需处理，防止接口重复调用
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }

        try {
            return figureCurrency(net, symbol, amontBig, toAddr, contract, txHash, status);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    /**
     *
     * @param net
     *            net
     * @param symbol
     *            symbol
     * @param amontBig
     *            amontBig
     * @param toAddr
     *            toAddr
     */
    private ResultMessageVo figureCurrency(String net, String symbol, BigDecimal amontBig, String toAddr,
        String contract, String txHash, String status) throws Exception {
        LambdaQueryWrapper<MoneyPaymentTransactionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MoneyPaymentTransactionEntity::getTransactionHash, txHash);
        MoneyPaymentTransactionEntity entity = moneyPaymentTransactionMapper.selectOne(queryWrapper);
        // 没有相关订单
        if (null == entity) {
            log.info("figureCurrencyRecharge没有订单信息,hash:{},toAddr:{},net:{}", txHash, toAddr, net);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
        if (Objects.equals(status, "4") || Objects.equals(status, "5")) {
            entity.setOrderStatus(PaymentConstant.accomplish);
            entity.setIfRead(NumberConstant.ZERO);
        } else {
            log.info("figureCurrencyRecharge状态不为成功,hash:{},toAddr:{},net:{},status:{}", txHash, toAddr, net, status);
            // 直接返回成功，失败不处理
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
        entity.setUpdateTime(System.currentTimeMillis());

        UserMo userMo = userMapper.selectById(entity.getUid());
        // 判断用户等级是否提升
        BigDecimal orderAmountCount =
            moneyPaymentTransactionMapper.getOrderAmountCount(entity.getUid()).add(entity.getEstimatedDepositAmount());
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserLevelMo>>() {}.getType();
        List<UserLevelMo> list = gson.fromJson(cache.get("user:level:list").toString(), type);
        UserLevelMo userLevelMo = list.stream()
            .filter(f -> ObjectUtils.isNotEmpty(f.getPromotionConditions())
                && orderAmountCount.compareTo(new BigDecimal(f.getPromotionConditions())) >= 0)
            .sorted(Comparator.comparing(co -> new BigDecimal(co.getPromotionConditions()), Comparator.reverseOrder()))
            .findFirst().orElse(null);

        // 通知资金管理的接口，订单完成
        BalanceChangeVo vo = new BalanceChangeVo();
        // 等级有变化
        if (ObjectUtils.isNotEmpty(userLevelMo) && !userLevelMo.getLevel().equals(userMo.getUserLevel())) {
            userMo.setUserLevel(userLevelMo.getLevel());
            userMapper.updateById(userMo);
            BigDecimal levelRewards = new BigDecimal(userLevelMo.getPromotionConditions()).multiply(
                new BigDecimal(userLevelMo.getAssetIncome()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN))
                .setScale(2, BigDecimal.ROUND_DOWN);
            if (ObjectUtils.isNotEmpty(entity.getRewardAmount())) {
                entity.setRewardAmount(entity.getRewardAmount().add(levelRewards).setScale(2, BigDecimal.ROUND_DOWN));
                entity.setRemark(entity.getRemark() + "," + "会员升级奖励");
            } else {
                entity.setRewardAmount(levelRewards);
                entity.setRemark("会员升级奖励");
            }
        }
        vo.setUid(entity.getUid());
        vo.setBusinessTypeEnums(BusinessTypeEnums.DEPOSIT);
        vo.setBusinessNo(entity.getOrderId());
        vo.setAmount(entity.getEstimatedDepositAmount());
        vo.setBonusAmount(entity.getRewardAmount());
        vo.setRemark(entity.getRemark());
        balanceService.transactionChangeBalanceAndSaveLog(vo);

        if (moneyPaymentTransactionMapper.updateById(entity) == 0) {
            log.info("figureCurrencyRecharge更新失败,hash:{},toAddr:{},net:{},status:{}", txHash, toAddr, net, status);
            return ResultUtil.error(ResultCodeEnums.ORDER_TAKE_ERROR);
        }

        // 充值成功放到消息队列
        Map<String, String> map = new HashMap<>();
        map.put("openId", entity.getOpenId());
        map.put("moneyPaymentTransactionId", entity.getId().toString());
        // 推送传递真实收到金额
        map.put("amount", entity.getEstimatedDepositAmount().add(entity.getRewardAmount()).toString());
        // Message sendMsg = new Message(RocketMqConstants.CHARGE_MSG,
        // JSONObject.toJSONString(map).getBytes(RemotingHelper.DEFAULT_CHARSET));
        // DefaultMQProducer producer = new DefaultMQProducer("ulla");
        // producer.setNamesrvAddr(producerAddress);
        // producer.start();
        // SendResult send = producer.send(sendMsg);
        // producer.shutdown();
        // log.info(send.toString());
        return ResultUtil.data(map);
    }

    /**
     * 订单状态完成后，补充奖励金额以及修改订单状态为已完成
     *
     * @param transaction
     *            补充参数VO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessageVo updateRewardAmount(MoneyPaymentTransactionEntity transaction) {
        MoneyPaymentTransactionEntity entity = new MoneyPaymentTransactionEntity();
        Calendar time = Calendar.getInstance();
        try {
            if (transaction != null) {
                MoneyPaymentTransactionEntity payent =
                    moneyPaymentTransactionMapper.getTransactionByOrderId(transaction.getOrderId());
                // 只有当订单状态为已付款，才可以做相关修改操作
                if (payent != null && (payent.getOrderStatus() == 1 || payent.getOrderStatus() == 5)) {
                    entity.setId(transaction.getId());
                    entity.setOrderId(transaction.getOrderId());
                    entity.setRewardAmount(transaction.getRewardAmount());
                    entity.setPreferentialAmount(transaction.getPreferentialAmount());
                    entity.setIsFirstOrder(transaction.getIsFirstOrder());
                    entity.setUpdateTime(time.getTimeInMillis());
                    // 所有订单处理完成以后，包括用户账号余额和奖励账号余额增加完成以后，修改订单状态为已完成
                    entity.setOrderStatus(PaymentConstant.accomplish);
                    moneyPaymentTransactionMapper.updateById(entity);
                } else {
                    return ResultUtil.error(500, "订单不存在或状态不在修改范围之内！");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(500, "订单状态完成后，补充奖励金额以及修改订单状态为已完成失败!");
        }
        return ResultUtil.success();
    }

}