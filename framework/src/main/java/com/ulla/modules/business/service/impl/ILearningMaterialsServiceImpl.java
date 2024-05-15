package com.ulla.modules.business.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.business.mapper.LearningMaterialsMapper;
import com.ulla.modules.business.mo.LearningMaterialsMo;
import com.ulla.modules.business.qo.LearningMaterialsQo;
import com.ulla.modules.business.service.ILearningMaterialsService;

@Service
public class ILearningMaterialsServiceImpl extends ServiceImpl<LearningMaterialsMapper, LearningMaterialsMo>
    implements ILearningMaterialsService {

    @Override
    public IPage<LearningMaterialsQo> pageList(Page<LearningMaterialsQo> page, LearningMaterialsQo qo) {
        QueryWrapper<LearningMaterialsQo> wrapper = new QueryWrapper<>();
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
        IPage<LearningMaterialsQo> privacyQoIPage = baseMapper.pageList(page, wrapper);
        return privacyQoIPage;
    }
}
