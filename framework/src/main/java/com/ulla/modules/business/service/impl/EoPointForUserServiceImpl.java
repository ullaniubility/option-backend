package com.ulla.modules.business.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.IdUtils;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.NumberConstant;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.service.BalanceService;
import com.ulla.modules.assets.vo.BalanceChangeVo;
import com.ulla.modules.business.mapper.EoPointForUserMapper;
import com.ulla.modules.business.mapper.EoPointLogMapper;
import com.ulla.modules.business.mapper.EoPointRulesMapper;
import com.ulla.modules.business.mapper.OrderMapper;
import com.ulla.modules.business.mo.EoPointForUserMo;
import com.ulla.modules.business.mo.EoPointLogMo;
import com.ulla.modules.business.mo.EoPointRulesMo;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.service.EoPointForUserService;
import com.ulla.modules.business.vo.EoPointRulesVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EoPointForUserServiceImpl extends ServiceImpl<EoPointForUserMapper, EoPointForUserMo>
        implements EoPointForUserService {

    final EoPointForUserMapper eoPointForUserMapper;

    final EoPointLogMapper eoPointLogMapper;

    final BalanceService balanceService;

    final OrderMapper orderMapper;

    final EoPointRulesMapper eoPointRulesMapper;

    @Override
    public ResultMessageVo getOrCreate(Long uid) {
        try {
            EoPointForUserMo mo = getPointForUserMo(uid);
            return ResultUtil.data(mo.getEoPoint());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(500, "system error");
        }
    }

    @Override
    public ResultMessageVo getLogPage(EoPointRulesVo vo, Long uid) {
        try {
            QueryWrapper<EoPointLogMo> wrapper = new QueryWrapper<>();
            wrapper.select("type", "eo_point", "create_time");
            wrapper.and(wp -> wp.eq("delete_flag", 0).or().isNull("delete_flag"));
            wrapper.eq("uid", uid);
            wrapper.orderByDesc("create_time");
            Page<EoPointLogMo> page = new Page<>(vo.getPage(), vo.getPageSize());
            return ResultUtil.data(eoPointLogMapper.selectPage(page, wrapper));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResultUtil.error(500, "system error");
        }
    }

    @Override
    public ResultMessageVo transToBalance(Long uid) {
        try {
            EoPointForUserMo mo = eoPointForUserMapper.getByUid(uid);
            BigDecimal eoPoint = mo.getEoPoint();
            mo.setEoPoint(new BigDecimal(NumberConstant.ZERO));
            eoPointForUserMapper.updateById(mo);
            BigDecimal bonusAmount = eoPoint.divide(new BigDecimal(NumberConstant.T100T), 2, BigDecimal.ROUND_DOWN);
            EoPointLogMo logMo = new EoPointLogMo();
            logMo.setEoPoint(new BigDecimal(NumberConstant.ZERO).subtract(eoPoint));
            logMo.setCreateTime(System.currentTimeMillis());
            logMo.setUid(uid);
            logMo.setType(NumberConstant.ONE);
            // 余额处理
            String simpleUUID = IdUtils.get8SimpleUUID();
            BalanceChangeVo balanceChangeVo = new BalanceChangeVo();
            balanceChangeVo.setBonusAmount(bonusAmount);
            balanceChangeVo.setUid(uid);
            balanceChangeVo.setBusinessNo(simpleUUID);
            balanceChangeVo.setBusinessTypeEnums(BusinessTypeEnums.POINT_EXCHANGE);
            balanceService.transactionChangeBalanceAndSaveLog(balanceChangeVo);
            eoPointLogMapper.insert(logMo);
            return ResultUtil.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(500, "system error");
        }
    }

    /**
     * 计算eo积分，查询规则， 统计用户历史操作金额总额
     *
     * @param list
     */
    @Override
    public void calculatePoint(List<OrderMo> list) {
        for (OrderMo order : list) {
            Long uid = order.getUid();
            EoPointForUserMo mo = getPointForUserMo(uid);
            List<EoPointRulesMo> rules =
                    eoPointRulesMapper.selectForAmount(mo.getAllAmount(), mo.getAllAmount().add(order.getOrderAmount()));
            if (!rules.isEmpty()) {
                for (EoPointRulesMo rule : rules) {
                    mo.setEoPoint(mo.getEoPoint().add(rule.getRewardPoints()));
                    EoPointLogMo logMo = new EoPointLogMo();
                    logMo.setBusinessNo(order.getOrderCode());
                    logMo.setUid(uid);
                    logMo.setCreateTime(System.currentTimeMillis());
                    logMo.setEoPoint(rule.getRewardPoints());
                    logMo.setType(NumberConstant.ZERO);
                    eoPointLogMapper.insert(logMo);
                }
            }
            mo.setAllAmount(mo.getAllAmount().add(order.getOrderAmount()));
            mo.setUpdateTime(System.currentTimeMillis());
            eoPointForUserMapper.updateById(mo);
        }
    }

    private EoPointForUserMo getPointForUserMo(Long uid) {
        EoPointForUserMo mo = eoPointForUserMapper.getByUid(uid);
        if (mo == null) {
            mo = new EoPointForUserMo();
            mo.setUid(uid);
            mo.setCreateTime(System.currentTimeMillis());
            QueryWrapper<OrderMo> wrapper = new QueryWrapper<>();
            wrapper.eq("uid", uid);
            wrapper.eq("status", NumberConstant.TWO);
            wrapper.eq("type", NumberConstant.ONE);
            List<OrderMo> list = orderMapper.selectList(wrapper);
            if (!list.isEmpty()) {
                for (OrderMo order : list) {
                    mo.setAllAmount(mo.getAllAmount().add(order.getOrderAmount()));
                }
            }
            eoPointForUserMapper.insert(mo);
        }
        return mo;
    }
}
