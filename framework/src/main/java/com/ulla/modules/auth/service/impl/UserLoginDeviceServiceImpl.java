package com.ulla.modules.auth.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.UserLoginDeviceMapper;
import com.ulla.modules.auth.service.UserLoginDeviceService;
import com.ulla.modules.business.mo.LoginDeviceMo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserLoginDeviceServiceImpl extends ServiceImpl<UserLoginDeviceMapper, LoginDeviceMo>
    implements UserLoginDeviceService {

    final UserLoginDeviceMapper deviceMapper;

    @Override
    public ResultMessageVo getLoginDevice(Long uid) {
        List<LoginDeviceMo> loginDeviceMos = deviceMapper.selectByUid(uid);
        return ResultUtil.data(loginDeviceMos);
    }
}
