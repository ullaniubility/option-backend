package com.ulla.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.auth.mapper.UserWalletConnectMapper;
import com.ulla.modules.auth.mo.UserWalletConnectMo;
import com.ulla.modules.auth.qo.UserWalletConnectQo;
import com.ulla.modules.auth.service.UserWalletConnectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserWalletConnectServiceImpl extends ServiceImpl<UserWalletConnectMapper, UserWalletConnectMo>
    implements UserWalletConnectService {

    @Override
    public IPage<UserWalletConnectMo> listByWithdrawal(Page<UserWalletConnectQo> page,
        UserWalletConnectQo walletConnectQo) {
        return baseMapper.listByWithdrawal(page, walletConnectQo);
    }
}
