package com.ulla.modules.auth.service;

import com.ulla.modules.auth.mo.UserMo;

/**
 * @author 1
 */
public interface ThirdLoginService {

    /**
     * 获取授权页面url
     * 
     * @param source
     *            可选值范围（"facebook","google","apple"）
     * @return
     */
    String getAuthorize(String source);

    /**
     * 获取用户信息
     * 
     * @param source
     *            可选值范围（"facebook","google","apple"）
     * @param code
     * @return
     */
    UserMo getUserInfo(String source, String code);
}
