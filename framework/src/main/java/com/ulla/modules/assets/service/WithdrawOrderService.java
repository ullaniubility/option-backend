package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.assets.mo.WithdrawOrderMo;
import com.ulla.modules.assets.vo.WithdrawOrderVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-11
 */
public interface WithdrawOrderService extends IService<WithdrawOrderMo> {

    /**
     * 创建出金订单
     * @param withdrawOrderMo
     * @return
     */
    WithdrawOrderVo createOrder(WithdrawOrderVo withdrawOrderMo);
}
