package com.ulla.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ulla.modules.auth.enums.AuthCommonSource;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.service.ThirdConfigService;
import com.ulla.modules.auth.service.ThirdLoginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 1
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThirdLoginServiceImpl implements ThirdLoginService {

    final ThirdConfigService configService;

    @Override
    public String getAuthorize(String source) {
        AuthCommonSource authCommonSource = AuthCommonSource.of(source);
        AbstractThirdPartyLogin thirdPartyLogin =
            AbstractThirdPartyLogin.build(authCommonSource, configService.getConfigInCacheByName(authCommonSource));
        return thirdPartyLogin.authorize();
    }

    @Override
    public UserMo getUserInfo(String source, String code) {
        AuthCommonSource authCommonSource = AuthCommonSource.of(source);
        AbstractThirdPartyLogin thirdPartyLogin =
            AbstractThirdPartyLogin.build(authCommonSource, configService.getConfigInCacheByName(authCommonSource));
        String accessToken = thirdPartyLogin.getAccessToken(code);
        return thirdPartyLogin.getUserInfo(accessToken);
    }
}
