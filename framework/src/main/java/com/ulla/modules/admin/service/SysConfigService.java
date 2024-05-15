package com.ulla.modules.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.SysConfigMo;
import com.ulla.modules.admin.qo.SysConfigQo;
import com.ulla.modules.business.vo.WithdrawalVo;

/**
 * @author {clj}
 * @Description {系统配置service}
 * @since {2023-2-10}
 */
public interface SysConfigService extends IService<SysConfigMo> {

    ResultMessageVo pageList(Page<SysConfigMo> page, SysConfigQo sysConfigQo);

    ResultMessageVo saveConfig(SysConfigMo sysConfigMo);

    SysConfigMo getSysConfigMoByKey(String key);

    ResultMessageVo getWithdrawal();

    ResultMessageVo updateWithdrawal(WithdrawalVo withdrawalVo, Long uid);
}
