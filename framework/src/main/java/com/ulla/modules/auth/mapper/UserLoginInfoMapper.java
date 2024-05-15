package com.ulla.modules.auth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.auth.mo.UserLoginInfoMo;

public interface UserLoginInfoMapper extends BaseMapper<UserLoginInfoMo> {

    @Select("select * from biz_user_login_info where uid = ${id} and if_guest =0 and delete_flag =0 ORDER BY create_time  DESC ")
    List<UserLoginInfoMo> selectByUserId(Long id);
}
