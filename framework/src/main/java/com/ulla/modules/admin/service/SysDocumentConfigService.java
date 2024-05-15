package com.ulla.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.SysDocumentConfigMo;

public interface SysDocumentConfigService extends IService<SysDocumentConfigMo> {
    ResultMessageVo saveConfig(SysDocumentConfigMo documentConfigMo);

    ResultMessageVo deleteConfig(SysDocumentConfigMo documentConfigMo);
}
