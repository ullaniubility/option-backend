package com.ulla.modules.admin.mapper;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.admin.mo.SysConfigMo;

public interface SysConfigMapper extends BaseMapper<SysConfigMo> {

    @Select("select config_value from sys_config where config_key = #{configKey}")
    String selectByKey(String configKey);

}
