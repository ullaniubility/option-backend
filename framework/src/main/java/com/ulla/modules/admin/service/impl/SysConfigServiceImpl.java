package com.ulla.modules.admin.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mapper.SysConfigMapper;
import com.ulla.modules.admin.mo.SysConfigMo;
import com.ulla.modules.admin.qo.SysConfigQo;
import com.ulla.modules.admin.service.SysConfigService;
import com.ulla.modules.business.mo.WithdrawalMo;
import com.ulla.modules.business.vo.WithdrawalVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfigMo> implements SysConfigService {

    final SysConfigMapper sysConfigMapper;

    @Override
    public ResultMessageVo pageList(Page<SysConfigMo> page, SysConfigQo qo) {
        QueryWrapper<SysConfigMo> wrapper = new QueryWrapper<>();
        wrapper.eq("config_type", "SYS");
        if (StringUtils.isNotEmpty(qo.getName())) {
            wrapper.like("config_name", qo.getName()).or().like("config_key", qo.getName()).or().like("config_value",
                qo.getName());
        }
        return ResultUtil.data(sysConfigMapper.selectPage(page, wrapper));
    }

    @Override
    public ResultMessageVo saveConfig(SysConfigMo sysConfigMo) {
        if (StringUtils.isEmpty(sysConfigMo.getConfigKey()) || StringUtils.isEmpty(sysConfigMo.getConfigName())
            || StringUtils.isEmpty(sysConfigMo.getConfigValue())) {
            return ResultUtil.error(4002, "Parameter error");
        }
        sysConfigMo.setConfigType("SYS");
        if (sysConfigMo.getId() == null) {
            this.save(sysConfigMo);
        } else {
            sysConfigMapper.updateById(sysConfigMo);
        }
        return ResultUtil.success(200, "保存成功");
    }

    @Override
    public SysConfigMo getSysConfigMoByKey(String key) {
        LambdaQueryWrapper<SysConfigMo> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysConfigMo::getConfigKey, key);
        return sysConfigMapper.selectOne(wrapper);
    }

    @Override
    public ResultMessageVo getWithdrawal() {
        try {
            SysConfigMo sysConfigMo = getSysConfigMoByKey("WITHDRAWAL");
            Gson gson = new Gson();
            WithdrawalMo withdrawalMo = gson.fromJson(sysConfigMo.getConfigValue(), WithdrawalMo.class);
            WithdrawalVo withdrawalVo = new WithdrawalVo();
            BeanUtils.copyProperties(sysConfigMo, withdrawalVo);
            withdrawalVo.setWithdrawalAmountPercent(withdrawalMo.getWithdrawalAmountPercent());
            withdrawalVo.setLowAmount(withdrawalMo.getLowAmount());
            return ResultUtil.data(withdrawalVo);
        } catch (Exception e) {
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    @Override
    public ResultMessageVo updateWithdrawal(WithdrawalVo withdrawalVo, Long uid) {
        try {
            SysConfigMo sysConfigMo = new SysConfigMo();
            Gson gson = new Gson();
            WithdrawalMo withdrawalMo = new WithdrawalMo();
            BeanUtils.copyProperties(withdrawalVo, withdrawalMo);
            BeanUtils.copyProperties(withdrawalVo, sysConfigMo);
            sysConfigMo.setConfigValue(gson.toJson(withdrawalMo));
            sysConfigMapper.updateById(sysConfigMo);
            return ResultUtil.data(sysConfigMo);
        } catch (Exception e) {
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

}
