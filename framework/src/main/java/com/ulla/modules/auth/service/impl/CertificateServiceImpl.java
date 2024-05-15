package com.ulla.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.CertificateMapper;
import com.ulla.modules.auth.mo.CertificateMo;
import com.ulla.modules.auth.qo.CertificateQo;
import com.ulla.modules.auth.service.CertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, CertificateMo>
    implements CertificateService {

    final CertificateMapper certificateMapper;

    @Override
    public IPage<CertificateMo> pageList(Page<CertificateMo> page, CertificateQo qo) {
        QueryWrapper<CertificateMo> wrapper = new QueryWrapper<>();
        if (qo.getName() != null) {
            wrapper.like("name", qo.getName()).or().like("name_en", qo.getName());
        }
        return certificateMapper.selectPage(page, wrapper);
    }

    @Override
    public ResultMessageVo getList() {
        return ResultUtil.data(certificateMapper.selectList(new QueryWrapper<>()));
    }
}
