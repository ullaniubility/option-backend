package com.ulla.modules.business.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.business.mo.LearningMaterialsMo;
import com.ulla.modules.business.qo.LearningMaterialsQo;

public interface LearningMaterialsMapper extends BaseMapper<LearningMaterialsMo> {
    IPage<LearningMaterialsQo> pageList(Page<LearningMaterialsQo> page,
        @Param("ew") QueryWrapper<LearningMaterialsQo> wrapper);
}
