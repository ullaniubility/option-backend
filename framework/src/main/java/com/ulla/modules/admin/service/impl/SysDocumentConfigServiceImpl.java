package com.ulla.modules.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mapper.SysDocumentConfigMapper;
import com.ulla.modules.admin.mo.SysDocumentConfigMo;
import com.ulla.modules.admin.service.SysDocumentConfigService;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysDocumentConfigServiceImpl extends ServiceImpl<SysDocumentConfigMapper, SysDocumentConfigMo>
    implements SysDocumentConfigService {

    final SysDocumentConfigMapper documentConfigMapper;

    @Override
    public ResultMessageVo saveConfig(SysDocumentConfigMo documentConfigMo) {
        if (StringUtils.isEmpty(documentConfigMo.getConfigKey())
            || StringUtils.isEmpty(documentConfigMo.getConfigName())
            || StringUtils.isEmpty(documentConfigMo.getConfigValue())) {
            return ResultUtil.error(4002, "Parameter error");
        }
        if (documentConfigMo.getId() == null) {
            this.save(documentConfigMo);
        } else {
            documentConfigMapper.updateById(documentConfigMo);
        }
        return ResultUtil.success(200, "Successfully saved");
    }

    @Override
    public ResultMessageVo deleteConfig(SysDocumentConfigMo documentConfigMo) {
        if (ObjectUtil.isNotEmpty(documentConfigMo.getId())) {
            documentConfigMapper.deleteConfig(documentConfigMo.getId());
        } else {
            return ResultUtil.error(4002, "Parameter error");
        }
        return ResultUtil.success(200, "Successfully deleted");
    }
}
