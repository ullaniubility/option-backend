package com.ulla.modules.auth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.business.mo.LoginDeviceMo;

public interface UserLoginDeviceMapper extends BaseMapper<LoginDeviceMo> {

    @Select("SELECT * FROM `biz_login_device` WHERE token= #{token} and uid=#{uid} ORDER BY create_time  DESC")
    LoginDeviceMo selectByIp(String token, Long uid);

    @Select("SELECT * FROM `biz_login_device` WHERE uid=#{uid} ORDER BY create_time  DESC")
    List<LoginDeviceMo> selectByUid(Long uid);

    @Select("SELECT * FROM `biz_login_device` WHERE uid=#{uid} ORDER BY create_time  ASC")
    List<LoginDeviceMo> selectByUidAsc(Long uid);
}
