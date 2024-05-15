package com.ulla.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.service.UserService;
import com.ulla.modules.auth.vo.UserEditVo;
import com.ulla.modules.auth.vo.UserVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @Description {用户controller}
 * @author {clj}
 * @since {2023-2-8}
 */
@Api(value = "用户注册/登录/忘记密码/重置密码", tags = {"用户注册/登录/忘记密码/重置密码"})
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    final UserService userService;

    @ApiOperation("验证用户验证码")
    @PostMapping(value = "/checkCode")
    public ResultMessageVo checkCode(@RequestBody UserVo userVo) {
        return userService.checkCode(UserUtil.getOpenId(), userVo.getCode(), userVo.getCodeType());
    }

    @ApiOperation("用户注册")
    @PostMapping(value = "/register")
    public ResultMessageVo register(@RequestBody UserVo userVo) {
        return userService.register(userVo.getMail(), userVo.getPassword());
    }

    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    public ResultMessageVo login(@RequestBody UserVo userVo) {
        return userService.login(userVo.getMail(), userVo.getPassword());
    }

    @ApiOperation("用户退出")
    @PostMapping(value = "/logout")
    public ResultMessageVo logout() {
        return userService.logout();
    }

    @ApiOperation("重置密码")
    @PostMapping(value = "/resetPassword")
    public ResultMessageVo resetPassword(@RequestBody UserVo userVo) {
        return userService.resetPassword(UserUtil.getOpenId(), userVo.getPassword());
    }

    @ApiOperation("忘记密码")
    @PostMapping(value = "/forgetPassword")
    public ResultMessageVo forgetPassword() {
        return userService.forgetPassword();
    }

    @ApiOperation("用户设备指纹接口/判断是否生成")
    @GetMapping(value = "/fingerprint")
    public ResultMessageVo fingerprint() {
        return userService.fingerprint();
    }

    @ApiOperation("获取用户详情资料")
    @GetMapping(value = "/getUserDetail")
    public ResultMessageVo getUserDetail() {
        return userService.getUserDetail();
    }

    @ApiOperation("编辑用户")
    @PostMapping(value = "/editUser")
    public ResultMessageVo editUser(@RequestBody UserEditVo userEditVo) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userEditVo, userVo);
        return userService.editUser(userVo);
    }

    @ApiOperation("设置头像")
    @PostMapping(value = "/setPortrait")
    public ResultMessageVo setPortrait(MultipartFile file) {
        return userService.setPortrait(file, UserUtil.getOpenId());
    }

    @ApiOperation("更换密码")
    @PostMapping(value = "/changePassword")
    public ResultMessageVo changePassword(@RequestBody UserVo userVo) {
        return userService.changePassword(UserUtil.getUid(), userVo.getMail(), userVo.getPassword(),
            userVo.getNewPassword());
    }

    @ApiOperation("获取系统配置")
    @GetMapping(value = "/getSysConfig")
    public ResultMessageVo getSysConfig(String configKey) {
        return userService.getSysConfig(configKey);
    }

    @ApiOperation("三方登录")
    @PostMapping(value = "/loginThird")
    public ResultMessageVo loginThird(@RequestBody JSONObject jsonObject) {
        return userService.loginThird(jsonObject);
    }

    @ApiOperation("获取弹窗配置")
    @GetMapping(value = "/getPopConfig")
    public ResultMessageVo getPopConfig(String configKey) {
        return userService.getPopConfig(configKey);
    }

}
