package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.business.mo.LearningMaterialsMo;
import com.ulla.modules.business.qo.LearningMaterialsQo;

public interface ILearningMaterialsService extends IService<LearningMaterialsMo> {
    IPage<LearningMaterialsQo> pageList(Page<LearningMaterialsQo> page, LearningMaterialsQo qo);
}
