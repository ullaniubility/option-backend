package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.auth.mo.UserAuthorityMo;

public interface UserAuthorityService extends IService<UserAuthorityMo> {
    UserAuthorityMo getAllow(Long uid);
}
