package com.ulla.modules.assets.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.BusinessNoUtil;
import com.ulla.common.vo.exception.ServiceException;
import com.ulla.modules.assets.enums.BusinessTypeEnums;
import com.ulla.modules.assets.mapper.WithdrawOrderMapper;
import com.ulla.modules.assets.mo.WithdrawOrderMo;
import com.ulla.modules.assets.service.WithdrawOrderService;
import com.ulla.modules.assets.vo.WithdrawOrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.ulla.constant.NumberConstant.ONE;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-11
 */
@Service
public class WithdrawOrderServiceImpl extends ServiceImpl<WithdrawOrderMapper, WithdrawOrderMo> implements WithdrawOrderService {

    @Override
    public WithdrawOrderVo createOrder(WithdrawOrderVo withdrawOrderVo) {
        if (withdrawOrderVo.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("出金金额必须大于0");
        }
        withdrawOrderVo.setOrderNo(BusinessNoUtil.genBusinessNo(BusinessTypeEnums.WITHDRAW.getPrefix()));
        withdrawOrderVo.setState(ONE);
        WithdrawOrderMo withdrawOrderMo = new WithdrawOrderMo();
        BeanUtils.copyProperties(withdrawOrderVo,withdrawOrderMo);
        save(withdrawOrderMo);
        return withdrawOrderVo;
    }
}
