package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mo.KYCMo;
import com.ulla.modules.auth.qo.KYCQo;

public interface KYCSerivce extends IService<KYCMo> {
    IPage<KYCMo> pageList(Page<KYCMo> page, KYCQo qo);

    ResultMessageVo review(KYCMo kycMo);
}
