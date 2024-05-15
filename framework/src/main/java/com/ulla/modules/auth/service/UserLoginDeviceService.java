package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.LoginDeviceMo;

public interface UserLoginDeviceService extends IService<LoginDeviceMo> {

    ResultMessageVo getLoginDevice(Long uid);
}
