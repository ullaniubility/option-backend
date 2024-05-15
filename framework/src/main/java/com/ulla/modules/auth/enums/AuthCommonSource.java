package com.ulla.modules.auth.enums;

import com.ulla.constant.RedisConstant;
import com.ulla.modules.auth.service.impl.AbstractThirdPartyLogin;
import com.ulla.modules.auth.service.impl.AppleLogin;
import com.ulla.modules.auth.service.impl.FacebookLogin;
import com.ulla.modules.auth.service.impl.GoogleLogin;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 1
 */

@Getter
@AllArgsConstructor
public enum AuthCommonSource implements AuthSource {
    /**
     * Facebook
     */
    FACEBOOK("facebook") {
        @Override
        public String authorize() {
            return "https://www.facebook.com/v16.0/dialog/oauth";
        }

        @Override
        public String accessToken() {
            return "https://graph.facebook.com/v16.0/oauth/access_token";
        }

        @Override
        public String userInfo() {
            return "https://graph.facebook.com/v16.0/me";
        }

        @Override
        public Class<? extends AbstractThirdPartyLogin> getTargetClass() {
            return FacebookLogin.class;
        }

        @Override
        public String cachePrefix() {
            return RedisConstant.ThirdConfig.GOOGLE;
        }
    },
    GOOGLE("google") {
        @Override
        public String authorize() {
            return "https://accounts.google.com/o/oauth2/v2/auth";
        }

        @Override
        public String accessToken() {
            return "https://www.googleapis.com/oauth2/v4/token";
        }

        @Override
        public String userInfo() {
            return "https://www.googleapis.com/oauth2/v3/userinfo";
        }

        @Override
        public Class<? extends AbstractThirdPartyLogin> getTargetClass() {
            return GoogleLogin.class;
        }

        @Override
        public String cachePrefix() {
            return RedisConstant.ThirdConfig.GOOGLE;
        }
    },
    APPLE("apple") {
        @Override
        public String authorize() {
            return "https://appleid.apple.com/auth/authorize";
        }

        @Override
        public String accessToken() {
            return "https://appleid.apple.com/auth/token";
        }

        /**
         * 不需要获取用户信息，这里的url是获取公钥的url
         * 
         * @return
         */
        @Override
        public String userInfo() {
            return "https://appleid.apple.com/auth/keys";
        }

        @Override
        public Class<? extends AbstractThirdPartyLogin> getTargetClass() {
            return AppleLogin.class;
        }

        @Override
        public String cachePrefix() {
            return RedisConstant.ThirdConfig.APPLE;
        }
    };

    private final String name;

    /**
     * 自己定义一个静态方法,通过name返回枚举常量对象
     */
    public static AuthCommonSource of(String name) {
        for (AuthCommonSource action : values()) {
            if (action.getName().equalsIgnoreCase(name)) {
                return action;
            }
        }
        throw new RuntimeException("无法获取第三方登录基本配置");
    }
}
