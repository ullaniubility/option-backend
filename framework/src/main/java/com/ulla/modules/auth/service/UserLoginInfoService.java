package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mo.UserLoginInfoMo;

/**
 * @author {clj}
 * @Description {用户service}
 * @since {2023-2-13}
 */
public interface UserLoginInfoService extends IService<UserLoginInfoMo> {

    ResultMessageVo getLoginInfo(String uid);
}
