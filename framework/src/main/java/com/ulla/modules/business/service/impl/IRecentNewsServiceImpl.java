package com.ulla.modules.business.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.business.mapper.RecentNewsMapper;
import com.ulla.modules.business.mo.RecentNewsMo;
import com.ulla.modules.business.qo.RecentNewsQo;
import com.ulla.modules.business.service.IRecentNewsService;

@Service
public class IRecentNewsServiceImpl extends ServiceImpl<RecentNewsMapper, RecentNewsMo> implements IRecentNewsService {
    @Override
    public IPage<RecentNewsQo> pageList(Page<RecentNewsQo> page, RecentNewsQo qo) {
        QueryWrapper<RecentNewsQo> wrapper = new QueryWrapper<>();
        if (ObjectUtils.isNotEmpty(qo.getTitle())) {
            wrapper.like("title", qo.getTitle());
        }
        if (ObjectUtils.isNotEmpty(qo.getVideoUrl())) {
            wrapper.eq("video_url", qo.getVideoUrl());
        }
        if (ObjectUtils.isNotEmpty(qo.getVideoTime())) {
            wrapper.eq("video_time", qo.getVideoTime());
        }
        if (ObjectUtils.isNotEmpty(qo.getVideoSize())) {
            wrapper.eq("video_size", qo.getVideoSize());
        }
        if (ObjectUtils.isNotEmpty(qo.getOperatorUid())) {
            wrapper.eq("operator_uid", qo.getOperatorUid());
        }
        if (ObjectUtils.isNotEmpty(qo.getBeginTime()) && ObjectUtils.isNotEmpty(qo.getEndTime())) {
            wrapper.between("create_time", qo.getBeginTime(), qo.getEndTime());
        }
        IPage<RecentNewsQo> privacyQoIPage = baseMapper.pageList(page, wrapper);
        return privacyQoIPage;
    }
}
