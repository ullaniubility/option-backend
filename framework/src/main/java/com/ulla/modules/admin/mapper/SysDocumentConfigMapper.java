package com.ulla.modules.admin.mapper;

import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.admin.mo.SysDocumentConfigMo;

public interface SysDocumentConfigMapper extends BaseMapper<SysDocumentConfigMo> {

    @Update("UPDATE sys_document_config SET delete_flag = 1 WHERE id = #{id}")
    void deleteConfig(Long id);
}
