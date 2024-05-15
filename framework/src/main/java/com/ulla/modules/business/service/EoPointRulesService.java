package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.EoPointRulesMo;
import com.ulla.modules.business.vo.EoPointRulesVo;

public interface EoPointRulesService extends IService<EoPointRulesMo> {

    ResultMessageVo delete(EoPointRulesMo mo);

    ResultMessageVo getPage(EoPointRulesVo vo);

    ResultMessageVo saveRules(EoPointRulesMo mo);
}
