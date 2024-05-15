package com.ulla.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.NumberConstant;
import com.ulla.modules.auth.mapper.KYCMapper;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.auth.mo.KYCMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.qo.KYCQo;
import com.ulla.modules.auth.service.KYCSerivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KYCServiceImpl extends ServiceImpl<KYCMapper, KYCMo> implements KYCSerivce {

    final KYCMapper kycMapper;

    final UserMapper userMapper;

    @Override
    public IPage<KYCMo> pageList(Page<KYCMo> page, KYCQo qo) {
        QueryWrapper<KYCMo> wrapper = new QueryWrapper<>();
        if (qo.getOpenId() != null) {
            wrapper.like("open_id", qo.getOpenId());
        }
        if (StringUtils.isNotEmpty(qo.getCertificateNum())) {
            wrapper.like("certificate_num", qo.getCertificateNum());
        }
        if (StringUtils.isNotEmpty(qo.getCountry())) {
            wrapper.like("country", qo.getCountry());
        }
        if (ObjectUtils.isNotEmpty(qo.getCertificateId())) {
            wrapper.eq("certificate_id", qo.getCertificateId());
        }
        if (ObjectUtils.isNotEmpty(qo.getStartTime()) && ObjectUtils.isNotEmpty(qo.getEndTime())) {
            wrapper.between("create_time", qo.getStartTime(), qo.getEndTime());
        }
        return kycMapper.selectPage(page, wrapper);
    }

    @Override
    public ResultMessageVo review(KYCMo kycMo) {
        try {
            if (kycMo.getId() == null || kycMo.getStatus() == null) {
                return ResultUtil.error(4002, "Parameter error");
            }
            KYCMo mo = kycMapper.selectById(kycMo.getId());
            UserMo user = userMapper.selectById(mo);
            if (user == null) {
                return ResultUtil.error(4002, "Parameter error");
            }
            if (kycMo.getStatus().equals(NumberConstant.ONE)) {
                mo.setStatus(kycMo.getStatus());
                user.setKycStatus(kycMo.getStatus());
                mo.setVerifyTime(System.currentTimeMillis());
                kycMapper.updateById(mo);
                userMapper.updateById(user);
            }
            if (kycMo.getStatus().equals(NumberConstant.TWO)) {
                if (StringUtils.isEmpty(kycMo.getComment())) {
                    return ResultUtil.error(4002, "Please describe the reason for the return");
                }
                mo.setComment(kycMo.getComment());
                mo.setStatus(kycMo.getStatus());
                mo.setVerifyTime(System.currentTimeMillis());
                user.setKycStatus(kycMo.getStatus());
                kycMapper.updateById(mo);
                userMapper.updateById(user);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(200, "保存成功");
    }
}
