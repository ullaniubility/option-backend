package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.EoPointForUserMo;
import com.ulla.modules.business.mo.OrderMo;
import com.ulla.modules.business.vo.EoPointRulesVo;

import java.util.List;

public interface EoPointForUserService extends IService<EoPointForUserMo> {
    ResultMessageVo getOrCreate(Long uid);

    ResultMessageVo getLogPage(EoPointRulesVo vo, Long uid);

    ResultMessageVo transToBalance(Long uid);

    void calculatePoint(List<OrderMo> list);
}
