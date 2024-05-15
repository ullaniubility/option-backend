package com.ulla.modules.business.mapper;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.business.mo.FileMo;

public interface FileMapper extends BaseMapper<FileMo> {

    @Select("select * from sys_file where uid = #{userId}")
    FileMo selectByUId(Long userId);
}
