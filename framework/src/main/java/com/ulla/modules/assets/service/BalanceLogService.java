package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.BalanceLogMo;
import com.ulla.modules.assets.vo.BalanceLogParameterVO;

/**
 * <p>
 * 用户资产流水表 服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
public interface BalanceLogService extends IService<BalanceLogMo> {

    /**
     * 通用创建余额变动日志
     * @param balanceLogMo
     * @param userId
     */
    void createLog(BalanceLogMo balanceLogMo, Long userId);

    /**
     * 资金流水表列表查询
     * @param vo  列表查询参数
     * @return
     */
    public ResultMessageVo balanceLogListByParamer(BalanceLogParameterVO vo);

}
