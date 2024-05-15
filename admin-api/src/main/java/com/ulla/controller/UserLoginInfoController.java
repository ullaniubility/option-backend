package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.service.UserLoginInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @Description {用户controller}
 * @author {clj}
 * @since {2023-2-13}
 */
@Api(value = "用户登录信息", tags = {"用户登录信息"})
@RestController
@RequestMapping("/sysLoginInfo")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserLoginInfoController {

    final UserLoginInfoService loginInfoService;

    @ApiOperation("根据uid获取登录信息")
    @GetMapping(value = "/getLoginInfo")
    public ResultMessageVo getLoginInfo(String openId) {
        return loginInfoService.getLoginInfo(openId);
    }

}
