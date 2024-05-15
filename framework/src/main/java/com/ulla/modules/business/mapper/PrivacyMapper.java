package com.ulla.modules.business.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.business.mo.PrivacyMo;
import com.ulla.modules.business.qo.PrivacyQo;

public interface PrivacyMapper extends BaseMapper<PrivacyMo> {
    IPage<PrivacyQo> pageList(Page<PrivacyQo> page, @Param("ew") QueryWrapper<PrivacyQo> wrapper);
}
