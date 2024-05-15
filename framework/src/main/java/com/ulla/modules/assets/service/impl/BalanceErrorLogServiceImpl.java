package com.ulla.modules.assets.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.assets.mapper.BalanceErrorLogMapper;
import com.ulla.modules.assets.mo.BalanceErrorLogMo;
import com.ulla.modules.assets.service.BalanceErrorLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户资产流水表 服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-06
 */
@Service
public class BalanceErrorLogServiceImpl extends ServiceImpl<BalanceErrorLogMapper, BalanceErrorLogMo> implements BalanceErrorLogService {

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean newTransactionalSave(BalanceErrorLogMo balanceErrorLogMo) {
        return save(balanceErrorLogMo);
    }
}
