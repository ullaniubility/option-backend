package com.ulla.modules.business.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.business.mo.RecentNewsMo;
import com.ulla.modules.business.qo.RecentNewsQo;

public interface RecentNewsMapper extends BaseMapper<RecentNewsMo> {
    IPage<RecentNewsQo> pageList(Page<RecentNewsQo> page, @Param("ew") QueryWrapper<RecentNewsQo> wrapper);
}
