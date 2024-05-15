package com.ulla.modules.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.business.mapper.SectionConfigMapper;
import com.ulla.modules.business.mo.SectionConfigMo;
import com.ulla.modules.business.qo.SectionConfigQo;
import com.ulla.modules.business.service.SectionConfigService;
import com.ulla.mybatis.util.PageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SectionConfigServiceImpl extends ServiceImpl<SectionConfigMapper, SectionConfigMo>
    implements SectionConfigService {

    final SectionConfigMapper sectionConfigMapper;

    @Override
    public IPage<SectionConfigMo> queryByParams(SectionConfigQo sectionConfigQo) {
        return this.page(PageUtil.initPage(sectionConfigQo), sectionConfigQo.queryWrapper());
    }

    @Override
    public List<SectionConfigMo> getList(SectionConfigQo sectionConfigQo) {
        return sectionConfigMapper.selectList(sectionConfigQo.queryWrapper());
    }
}
