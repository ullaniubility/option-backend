package com.ulla.modules.auth.service.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.ulla.modules.auth.enums.AuthCommonSource;
import com.ulla.modules.auth.enums.AuthGoogleScope;
import com.ulla.modules.auth.enums.AuthScope;
import com.ulla.modules.auth.mo.ThirdConfigMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.utils.HttpUtils;
import com.ulla.modules.auth.utils.UrlBuilder;
import com.xkcoding.http.support.HttpHeader;

/**
 * @author 1
 */
public class GoogleLogin extends AbstractThirdPartyLogin {

    public GoogleLogin(ThirdConfigMo config) {
        super(AuthCommonSource.GOOGLE, config);
    }

    @Override
    public String authorize() {
        return UrlBuilder.fromBaseUrl(source.authorize()).queryParam("response_type", "code")
            .queryParam("client_id", config.getClientId()).queryParam("redirect_uri", config.getRedirectUri())
            .queryParam("access_type", "offline")
            .queryParam("scope",
                this.getScopes(" ", false, Arrays.stream(AuthGoogleScope.values()).filter(AuthGoogleScope::isDefault)
                    .map(AuthScope::getScope).collect(Collectors.toList())))
            .queryParam("prompt", "select_account").build();
    }

    @Override
    public String getAccessToken(String code) {
        String response = new HttpUtils().post(accessTokenUrl(code)).getBody();
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        this.checkResponse(accessTokenObject);
        return accessTokenObject.getString("access_token");
    }

    @Override
    public UserMo getUserInfo(String accessToken) {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.add("Authorization", "Bearer " + accessToken);
        String userInfo = new HttpUtils()
            .post(UrlBuilder.fromBaseUrl(source.userInfo()).queryParam("access_token", accessToken).build(), null,
                httpHeader)
            .getBody();
        JSONObject object = JSONObject.parseObject(userInfo);
        this.checkResponse(object);
        UserMo user = new UserMo();
        user.setMail(object.getString("email"));
        user.setNickName(object.getString("name"));
        return user;
    }

}
