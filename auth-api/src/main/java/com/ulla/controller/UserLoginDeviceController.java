package com.ulla.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ulla.cache.Cache;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.UserLoginDeviceMapper;
import com.ulla.modules.auth.service.UserLoginDeviceService;
import com.ulla.modules.business.mo.LoginDeviceMo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(value = "用户登录设备信息", tags = {"用户登录设备信息"})
@RestController
@RequestMapping("/loginDevice")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserLoginDeviceController {

    final UserLoginDeviceService deviceService;

    final UserLoginDeviceMapper deviceMapper;

    final Cache cache;

    @ApiOperation("根据uid获取登录信息")
    @GetMapping(value = "/getLoginDevice")
    public ResultMessageVo getLoginDevice() {
        System.out.println(UserUtil.getUid() + "*************");
        return deviceService.getLoginDevice(UserUtil.getUid());
    }

    /**
     * 中止其他会话
     */
    @ApiOperation("中止其他会话")
    @GetMapping(value = "/getStopLogin")
    public ResultMessageVo getStopLogin() {
        List<LoginDeviceMo> loginDeviceMos = deviceMapper.selectByUid(UserUtil.getUid());
        if (ObjectUtils.isEmpty(loginDeviceMos)) {
            return ResultUtil.error(20003, "用户没有登录设备");
        } else {
            for (LoginDeviceMo mo : loginDeviceMos) {
                if (!mo.getToken().equals(UserUtil.getToken())) {
                    deviceMapper.deleteById(mo.getId());
                    cache.remove(mo.getToken());
                }
            }
            return ResultUtil.success();
        }
    }
}
