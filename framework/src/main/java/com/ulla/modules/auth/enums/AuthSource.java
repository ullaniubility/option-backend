package com.ulla.modules.auth.enums;

import com.ulla.modules.auth.service.impl.AbstractThirdPartyLogin;

/**
 * @author 1
 */
public interface AuthSource {

    /**
     * 授权的api
     *
     * @return url
     */
    String authorize();

    /**
     * 获取accessToken的api
     *
     * @return url
     */
    String accessToken();

    /**
     * 获取用户信息的api
     *
     * @return url
     */
    String userInfo();

    /**
     * 平台对应的实现类
     *
     * @return class
     */
    Class<? extends AbstractThirdPartyLogin> getTargetClass();

    /**
     * 缓存前缀
     * 
     * @return
     */
    String cachePrefix();
}
