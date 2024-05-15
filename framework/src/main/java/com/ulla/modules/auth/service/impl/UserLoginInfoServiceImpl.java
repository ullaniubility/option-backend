package com.ulla.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.UserLoginInfoMapper;
import com.ulla.modules.auth.mapper.UserMapper;
import com.ulla.modules.auth.mo.UserLoginInfoMo;
import com.ulla.modules.auth.mo.UserMo;
import com.ulla.modules.auth.service.UserLoginInfoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserLoginInfoServiceImpl extends ServiceImpl<UserLoginInfoMapper, UserLoginInfoMo>
    implements UserLoginInfoService {

    final UserMapper userMapper;

    final UserLoginInfoMapper infoMapper;

    @Override
    public ResultMessageVo getLoginInfo(String openId) {
        UserMo userMo = userMapper.selectByOpenId(openId);
        return ResultUtil.data(infoMapper.selectByUserId(userMo.getUid()));
    }
}
