package com.ulla.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.auth.mapper.UserAuthorityMapper;
import com.ulla.modules.auth.mo.UserAuthorityMo;
import com.ulla.modules.auth.service.UserAuthorityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAuthorityServiceImpl extends ServiceImpl<UserAuthorityMapper, UserAuthorityMo>
    implements UserAuthorityService {
    @Override
    public UserAuthorityMo getAllow(Long uid) {
        UserAuthorityMo one = this.getOne(Wrappers.<UserAuthorityMo>query().lambda().eq(UserAuthorityMo::getUid, uid));
        if (ObjectUtils.isEmpty(one)) {
            UserAuthorityMo authorityMo = new UserAuthorityMo();
            authorityMo.setUid(uid);
            save(authorityMo);
        }
        return this.getOne(Wrappers.<UserAuthorityMo>query().lambda().eq(UserAuthorityMo::getUid, uid));
    }
}
