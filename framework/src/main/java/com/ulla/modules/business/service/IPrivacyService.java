package com.ulla.modules.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.business.mo.PrivacyMo;
import com.ulla.modules.business.qo.PrivacyQo;

/**
 * @author pag
 */
public interface IPrivacyService extends IService<PrivacyMo> {

    IPage<PrivacyQo> pageList(Page<PrivacyQo> page, PrivacyQo qo);

    IPage<PrivacyQo> payPage(Page<PrivacyQo> page, PrivacyQo qo);
}
