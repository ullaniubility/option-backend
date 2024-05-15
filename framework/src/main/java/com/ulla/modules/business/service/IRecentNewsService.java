package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.business.mo.RecentNewsMo;
import com.ulla.modules.business.qo.RecentNewsQo;

public interface IRecentNewsService extends IService<RecentNewsMo> {
    IPage<RecentNewsQo> pageList(Page<RecentNewsQo> page, RecentNewsQo qo);
}
