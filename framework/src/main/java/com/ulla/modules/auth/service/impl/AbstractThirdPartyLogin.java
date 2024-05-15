package com.ulla.modules.auth.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ulla.modules.auth.enums.AuthCommonSource;
import com.ulla.modules.auth.enums.AuthSource;
import com.ulla.modules.auth.mo.ThirdConfigMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.utils.UrlBuilder;
import com.xkcoding.http.util.UrlUtil;

/**
 * @author 1
 */
public abstract class AbstractThirdPartyLogin {
    protected AuthSource source;
    protected ThirdConfigMo config;

    public AbstractThirdPartyLogin(AuthSource source, ThirdConfigMo config) {
        this.source = source;
        this.config = config;
    }

    /**
     * 获取授权url
     * 
     * @return
     */
    public abstract String authorize();

    /**
     * 根据code值获取用户的AccessToken
     * 
     * @param code
     * @return
     */
    public abstract String getAccessToken(String code);

    /**
     * 获取用户信息
     * 
     * @param accessToken
     * @return
     */
    public abstract UserMo getUserInfo(String accessToken);

    /**
     * 检查响应内容是否正确
     *
     * @param object
     *            请求响应内容
     */
    protected void checkResponse(JSONObject object) {
        if (object.containsKey("error")) {
            throw new RuntimeException(object.getJSONObject("error").getString("message"));
        }
    }

    /**
     * 获取以 {@code separator}分割过后的 scope 信息
     *
     * @param separator
     *            多个 {@code scope} 间的分隔符
     * @param encode
     *            是否 encode 编码
     * @param defaultScopes
     *            默认的 scope， 当客户端没有配置 {@code scopes} 时启用
     * @return String
     * @since 1.16.7
     */
    protected String getScopes(String separator, boolean encode, List<String> defaultScopes) {
        if (null == defaultScopes || defaultScopes.isEmpty()) {
            return "";
        }
        if (null == separator) {
            // 默认为空格
            separator = " ";
        }
        String scopeStr = String.join(separator, defaultScopes);
        return encode ? UrlUtil.urlEncode(scopeStr) : scopeStr;
    }

    /**
     * 返回获取accessToken的url
     *
     * @param code
     *            授权码
     * @return 返回获取accessToken的url
     */
    protected String accessTokenUrl(String code) {
        return UrlBuilder.fromBaseUrl(source.accessToken()).queryParam("code", code)
            .queryParam("client_id", config.getClientId()).queryParam("client_secret", config.getClientSecret())
            .queryParam("grant_type", "authorization_code").queryParam("redirect_uri", config.getRedirectUri()).build();
    }

    public static AbstractThirdPartyLogin build(AuthCommonSource source, ThirdConfigMo config) {
        try {
            return source.getTargetClass().getConstructor(ThirdConfigMo.class).newInstance(config);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException
            | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
