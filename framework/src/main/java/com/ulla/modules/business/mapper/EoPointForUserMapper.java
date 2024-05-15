package com.ulla.modules.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.business.mo.EoPointForUserMo;
import org.apache.ibatis.annotations.Select;

public interface EoPointForUserMapper extends BaseMapper<EoPointForUserMo> {

    @Select("select * from biz_eo_point_for_user where uid = #{uid}")
    EoPointForUserMo getByUid(Long uid);
}
