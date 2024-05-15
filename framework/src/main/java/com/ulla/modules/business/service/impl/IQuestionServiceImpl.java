package com.ulla.modules.business.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.business.mapper.QuestionMapper;
import com.ulla.modules.business.mo.QuestionMo;
import com.ulla.modules.business.qo.QuestionQo;
import com.ulla.modules.business.service.IQuestionService;

@Service
public class IQuestionServiceImpl extends ServiceImpl<QuestionMapper, QuestionMo> implements IQuestionService {

    @Override
    public IPage<QuestionQo> pageList(Page<QuestionQo> page, QuestionQo qo) {
        QueryWrapper<QuestionQo> wrapper = new QueryWrapper<>();
        if (ObjectUtils.isNotEmpty(qo.getQuestion())) {
            wrapper.like("question", qo.getQuestion());
        }
        if (ObjectUtils.isNotEmpty(qo.getAnswer())) {
            wrapper.like("answer", qo.getAnswer());
        }
        if (ObjectUtils.isNotEmpty(qo.getOperatorUid())) {
            wrapper.eq("operator_uid", qo.getOperatorUid());
        }
        if (ObjectUtils.isNotEmpty(qo.getBeginTime()) && ObjectUtils.isNotEmpty(qo.getEndTime())) {
            wrapper.between("create_time", qo.getBeginTime(), qo.getEndTime());
        }
        IPage<QuestionQo> privacyQoIPage = baseMapper.pageList(page, wrapper);
        return privacyQoIPage;
    }

}