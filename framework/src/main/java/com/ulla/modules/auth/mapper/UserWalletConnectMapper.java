package com.ulla.modules.auth.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.auth.mo.UserWalletConnectMo;
import com.ulla.modules.auth.qo.UserWalletConnectQo;

/**
 * @author zhuyongdong
 * @Description 用户WalletConnect钱包地址
 * @since 2023-04-25 15:09:11
 */
public interface UserWalletConnectMapper extends BaseMapper<UserWalletConnectMo> {

    IPage<UserWalletConnectMo> listByWithdrawal(Page<UserWalletConnectQo> page,
        @Param("query") UserWalletConnectMo walletConnectMo);
}
