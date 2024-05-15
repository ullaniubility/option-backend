package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.business.mo.QuestionMo;
import com.ulla.modules.business.qo.QuestionQo;

public interface IQuestionService extends IService<QuestionMo> {
    IPage<QuestionQo> pageList(Page<QuestionQo> page, QuestionQo qo);
}
