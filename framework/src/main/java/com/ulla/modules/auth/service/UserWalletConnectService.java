package com.ulla.modules.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.auth.mo.UserWalletConnectMo;
import com.ulla.modules.auth.qo.UserWalletConnectQo;

public interface UserWalletConnectService extends IService<UserWalletConnectMo> {
    IPage<UserWalletConnectMo> listByWithdrawal(Page<UserWalletConnectQo> page,
        UserWalletConnectQo userWalletConnectQo);
}
