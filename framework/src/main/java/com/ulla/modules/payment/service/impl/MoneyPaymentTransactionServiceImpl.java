package com.ulla.modules.payment.service.impl;

import static com.ulla.constant.NumberConstant.ONE;
import static com.ulla.constant.NumberConstant.ZERO;

import java.math.BigDecimal;
import java.util.*;

import com.ulla.constant.NumberConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.PageUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.PaymentConstant;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.assets.vo.DepositOrderVo;
import com.ulla.modules.payment.entity.MoneyPaymentTransactionEntity;
import com.ulla.modules.payment.entity.ReasonReissueInfoEntity;
import com.ulla.modules.payment.mapper.MoneyPaymentTransactionMapper;
import com.ulla.modules.payment.mapper.ReasonReissueInfoMapper;
import com.ulla.modules.payment.service.MoneyPaymentTransactionService;
import com.ulla.modules.payment.vo.MoneyPaymentTransactionVO;
import com.ulla.modules.payment.vo.TransactionParamerVO;

@Service("moneyPaymentTransactionService")
public class MoneyPaymentTransactionServiceImpl
    extends ServiceImpl<MoneyPaymentTransactionMapper, MoneyPaymentTransactionEntity>
    implements MoneyPaymentTransactionService {

    @Autowired
    private MoneyPaymentTransactionMapper moneyPaymentTransactionMapper;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private ReasonReissueInfoMapper reasonReissueInfoMapper;

    /**
     * 入金订单列表查询
     * 
     * @param vo
     *            列表查询参数
     * @return
     */
    public ResultMessageVo transactionListByParamer(TransactionParamerVO vo) {
        Integer page = (vo.getPage() - 1) * vo.getPageSize();
        vo.setPage(page);
        List<TransactionParamerVO> list = moneyPaymentTransactionMapper.getTransactionListByParamer(vo);
        Integer listCount = moneyPaymentTransactionMapper.getTransactionListByParamerCount(vo);
        PageUtils pageUtils = new PageUtils(list, listCount, vo.getPageSize(), page);
        return ResultUtil.data(pageUtils);
    }

    /**
     * 入金订单详情查询
     * 
     * @param orderId
     *            订单Id
     * @return
     */
    public ResultMessageVo getTransactionInfoById(String orderId) {
        MoneyPaymentTransactionVO vo = new MoneyPaymentTransactionVO();
        MoneyPaymentTransactionEntity entity = moneyPaymentTransactionMapper.getTransactionByOrderId(orderId);
        BeanUtils.copyProperties(entity, vo);
        return ResultUtil.data(vo);
    }

    /**
     * 订单失效或者失败时 一键补发操作
     * 
     * @param orderId
     *            订单Id
     * @return
     */
    public ResultMessageVo supplyAgain(String orderId, String reasonInfo) {
        MoneyPaymentTransactionEntity transaction = moneyPaymentTransactionMapper.getTransactionByOrderId(orderId);
        if (transaction != null) {
            // 获取订单状态 0：待付款1：已付款2：已完成3：已失效4：已失败
            Integer orderStatus = transaction.getOrderStatus();
            // 当订单状态在已失效或者已失败情况才具备补发操作的条件
            if (orderStatus == 3 || orderStatus == 4) {

                List<ReasonReissueInfoEntity> list =
                    reasonReissueInfoMapper.getReasonReissueList(transaction.getOrderId());
                if (CollectionUtils.isNotEmpty(list)) {
                    return ResultUtil.error(500, "该订单已补发过，无法重复补发");

                }
                // 通知资金管理的接口，给用户增加余额
                BalanceChangeVo balanceVO = new BalanceChangeVo();
                balanceVO.setUid(transaction.getUid());
                // 变动的真实余额 = 页面选择的入金金额
                balanceVO.setAmount(transaction.getEstimatedDepositAmount());
                // 变动的奖金余额 = 奖励金额
                balanceVO.setBonusAmount(transaction.getRewardAmount());
                balanceVO.setBusinessTypeEnums(BusinessTypeEnums.DEPOSIT);
                balanceVO.setBusinessNo(transaction.getOrderId());

                // 订单成功调用改变首充状态和改变余额
                // 返回是否首充，false不是首充订单，返回true是首充订单
                boolean isFirstOrder = balanceService.checkOrderAndChangeBalance(balanceVO);
                MoneyPaymentTransactionEntity tronEntity = new MoneyPaymentTransactionEntity();
                // 补发完成后修改订单为已完成
                tronEntity.setId(transaction.getId());
                tronEntity.setOrderStatus(PaymentConstant.accomplish);
                moneyPaymentTransactionMapper.updateById(tronEntity);
                // 补发理由新增
                ReasonReissueInfoEntity entity = new ReasonReissueInfoEntity();
                entity.setOrderId(transaction.getOrderId());
                entity.setReasonInfo(reasonInfo);
                entity.setCreateTime(new Date());
                reasonReissueInfoMapper.insert(entity);
            } else {
                return ResultUtil.error(500, "订单状态不在可以补发的状态内！");
            }
        } else {
            return ResultUtil.error(500, "订单详情无法获取！");
        }
        return ResultUtil.success();
    }

    /**
     * * 旧的查看入金是否成功接口*
     * 
     * @param uid
     *            用户Id
     * @return
     */
    @Override
    public ResultMessageVo getDepositAmount(Long uid) {
        List<DepositOrderVo> depositAmount = balanceService.getDepositAmount(uid);
        if (CollectionUtils.isEmpty(depositAmount)) {
            return ResultUtil.success();
        }
        DepositOrderVo depositOrderVo = depositAmount.get(depositAmount.size() - ONE);
        long count = count(new LambdaQueryWrapper<MoneyPaymentTransactionEntity>()
            .gt(MoneyPaymentTransactionEntity::getCreateTime, depositOrderVo.getCreateTime()));
        Map<String, Object> map = new HashMap<>(2);
        map.put("orders", depositAmount);
        map.put("flag", false);
        if (count > ZERO) {
            map.put("flag", true);
        }
        return ResultUtil.data(map);
    }

    /**
     * 新的查看入金是否成功接口(有成功并未推送消息的情况直接 将充值金额返回方便前端提示)
     * 
     * @param openId
     * @return
     */
    @Override
    public ResultMessageVo getDepositAmount(String openId) {
        List<MoneyPaymentTransactionEntity> noReadList = moneyPaymentTransactionMapper.getNoReadList(openId);
        List<BigDecimal> amountList = new ArrayList<>();
        if (!noReadList.isEmpty()) {
            for (MoneyPaymentTransactionEntity entity : noReadList) {
                amountList.add(entity.getEstimatedDepositAmount().add(entity.getRewardAmount()));
                entity.setIfRead(NumberConstant.TWO);
                moneyPaymentTransactionMapper.updateById(entity);
            }
        }
        return ResultUtil.data(amountList);
    }

    @Override
    public ResultMessageVo getWallectByOrderId(String orderId) {
        return ResultUtil.data(baseMapper.getWallectByOrderId(orderId));
    }

}