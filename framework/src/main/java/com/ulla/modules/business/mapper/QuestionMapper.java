package com.ulla.modules.business.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.business.mo.QuestionMo;
import com.ulla.modules.business.qo.QuestionQo;

public interface QuestionMapper extends BaseMapper<QuestionMo> {
    IPage<QuestionQo> pageList(Page<QuestionQo> page, @Param("ew") QueryWrapper<QuestionQo> wrapper);
}
