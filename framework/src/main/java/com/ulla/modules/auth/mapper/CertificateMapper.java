package com.ulla.modules.auth.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.auth.mo.CertificateMo;

@Mapper
public interface CertificateMapper extends BaseMapper<CertificateMo> {}
