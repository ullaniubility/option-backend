package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.auth.enums.AuthCommonSource;
import com.ulla.modules.auth.mo.ThirdConfigMo;

/**
 * @author 1
 */
public interface ThirdConfigService extends IService<ThirdConfigMo> {
    /**
     * 根据第三方登录平台获得基础配置
     * 
     * @param source
     * @return
     */
    ThirdConfigMo getConfigInCacheByName(AuthCommonSource source);

    /**
     * 清除第三方登录平台基础配置的缓存
     * 
     * @param source
     * @return
     */
    void cleanCache(AuthCommonSource source);
}
