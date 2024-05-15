package com.ulla.modules.auth.service.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.ulla.modules.auth.enums.AuthCommonSource;
import com.ulla.modules.auth.enums.AuthFacebookScope;
import com.ulla.modules.auth.enums.AuthScope;
import com.ulla.modules.auth.mo.ThirdConfigMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.utils.HttpUtils;
import com.ulla.modules.auth.utils.UrlBuilder;

/**
 * @author 1
 */
public class FacebookLogin extends AbstractThirdPartyLogin {

    public FacebookLogin(ThirdConfigMo config) {
        super(AuthCommonSource.FACEBOOK, config);
    }

    @Override
    public String authorize() {
        return UrlBuilder.fromBaseUrl(source.authorize()).queryParam("response_type", "code")
            .queryParam("client_id", config.getClientId()).queryParam("redirect_uri", config.getRedirectUri())
            .queryParam("scope",
                this.getScopes(",", false, Arrays.stream(AuthFacebookScope.values())
                    .filter(AuthFacebookScope::isDefault).map(AuthScope::getScope).collect(Collectors.toList())))
            .build();
    }

    @Override
    public String getAccessToken(String code) {
        String response = new HttpUtils().post(accessTokenUrl(code)).getBody();
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        checkResponse(accessTokenObject);
        return accessTokenObject.getString("access_token");
    }

    @Override
    public UserMo getUserInfo(String accessToken) {
        String userInfo = UrlBuilder.fromBaseUrl(source.userInfo()).queryParam("access_token", accessToken).build();
        JSONObject object = JSONObject.parseObject(userInfo);
        checkResponse(object);
        UserMo user = new UserMo();
        user.setMail(object.getString("email"));
        user.setNickName(object.getString("name"));
        return user;
    }

    private String getUserPicture(JSONObject object) {
        String picture = null;
        if (object.containsKey("picture")) {
            JSONObject pictureObj = object.getJSONObject("picture");
            pictureObj = pictureObj.getJSONObject("data");
            if (null != pictureObj) {
                picture = pictureObj.getString("url");
            }
        }
        return picture;
    }
}
