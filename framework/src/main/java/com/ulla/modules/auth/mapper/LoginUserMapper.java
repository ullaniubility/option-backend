package com.ulla.modules.auth.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.auth.mo.LoginUserMo;

@Mapper
public interface LoginUserMapper extends BaseMapper<LoginUserMo> {

    @Select("select * from biz_login_user where open_id = #{openId}")
    LoginUserMo selectByOpenId(String openId);

    @Select("select * from biz_login_user where open_id = #{openId} and token = #{token}")
    LoginUserMo selectByOpenIdAndToken(String openId, String token);

    @Delete("delete from biz_login_user where token = #{token}")
    void deleteByOpenId(String token);
}
