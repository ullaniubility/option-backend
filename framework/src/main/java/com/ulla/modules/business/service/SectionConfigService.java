package com.ulla.modules.business.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.business.mo.SectionConfigMo;
import com.ulla.modules.business.qo.SectionConfigQo;

/**
 * @Description 结算时间点配置
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
public interface SectionConfigService extends IService<SectionConfigMo> {
    IPage<SectionConfigMo> queryByParams(SectionConfigQo sectionConfigQo);

    List<SectionConfigMo> getList(SectionConfigQo sectionConfigQo);
}
