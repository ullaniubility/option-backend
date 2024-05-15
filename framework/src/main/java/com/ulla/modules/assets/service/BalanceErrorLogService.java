package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.assets.mo.BalanceErrorLogMo;

/**
 * <p>
 * 用户资产流水表 服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-06
 */
public interface BalanceErrorLogService extends IService<BalanceErrorLogMo> {

    /**
     * 创建新事务保存错误日志
     * @param balanceErrorLogMo
     * @return
     */
    Boolean newTransactionalSave(BalanceErrorLogMo balanceErrorLogMo);
}
