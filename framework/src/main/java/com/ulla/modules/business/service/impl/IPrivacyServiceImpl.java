package com.ulla.modules.business.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.business.mapper.PrivacyMapper;
import com.ulla.modules.business.mo.PrivacyMo;
import com.ulla.modules.business.qo.PrivacyQo;
import com.ulla.modules.business.service.IPrivacyService;

@Service
public class IPrivacyServiceImpl extends ServiceImpl<PrivacyMapper, PrivacyMo> implements IPrivacyService {
    @Override
    public IPage<PrivacyQo> pageList(Page<PrivacyQo> page, PrivacyQo qo) {
        QueryWrapper<PrivacyQo> wrapper = new QueryWrapper<>();
        wrapper.eq("form", 0);
        // 根据标题内容模糊搜索
        // 根据时间搜索
        // 根据编辑人搜索
        if (ObjectUtils.isNotEmpty(qo.getTitle())) {
            wrapper.like("title", qo.getTitle());
        }
        if (ObjectUtils.isNotEmpty(qo.getContent())) {
            wrapper.like("content", qo.getContent());
        }
        if (ObjectUtils.isNotEmpty(qo.getOperatorUid())) {
            wrapper.eq("operator_uid", qo.getOperatorUid());
        }
        if (ObjectUtils.isNotEmpty(qo.getBeginTime()) && ObjectUtils.isNotEmpty(qo.getEndTime())) {
            wrapper.between("create_time", qo.getBeginTime(), qo.getEndTime());
        }
        IPage<PrivacyQo> privacyQoIPage = baseMapper.pageList(page, wrapper);
        return privacyQoIPage;
    }

    @Override
    public IPage<PrivacyQo> payPage(Page<PrivacyQo> page, PrivacyQo qo) {
        QueryWrapper<PrivacyQo> wrapper = new QueryWrapper<>();
        wrapper.eq("form", 1);
        // 根据标题内容模糊搜索
        // 根据时间搜索
        // 根据编辑人搜索
        if (ObjectUtils.isNotEmpty(qo.getTitle())) {
            wrapper.like("title", qo.getTitle());
        }
        if (ObjectUtils.isNotEmpty(qo.getContent())) {
            wrapper.like("content", qo.getContent());
        }
        if (ObjectUtils.isNotEmpty(qo.getOperatorUid())) {
            wrapper.eq("operator_uid", qo.getOperatorUid());
        }
        if (ObjectUtils.isNotEmpty(qo.getBeginTime()) && ObjectUtils.isNotEmpty(qo.getEndTime())) {
            wrapper.between("create_time", qo.getBeginTime(), qo.getEndTime());
        }
        IPage<PrivacyQo> privacyQoIPage = baseMapper.pageList(page, wrapper);
        return privacyQoIPage;
    }
}
