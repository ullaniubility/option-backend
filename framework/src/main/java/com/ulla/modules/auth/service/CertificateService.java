package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mo.CertificateMo;
import com.ulla.modules.auth.qo.CertificateQo;

public interface CertificateService extends IService<CertificateMo> {

    IPage<CertificateMo> pageList(Page<CertificateMo> page, CertificateQo qo);

    ResultMessageVo getList();
}
