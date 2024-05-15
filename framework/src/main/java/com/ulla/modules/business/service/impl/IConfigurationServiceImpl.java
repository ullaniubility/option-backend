package com.ulla.modules.business.service.impl;

import com.ulla.modules.business.mapper.ConfigurationMapper;
import com.ulla.modules.business.mo.ConfigurationMo;
import com.ulla.modules.business.service.IConfigurationService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class IConfigurationServiceImpl extends ServiceImpl<ConfigurationMapper, ConfigurationMo>
    implements IConfigurationService {

}
