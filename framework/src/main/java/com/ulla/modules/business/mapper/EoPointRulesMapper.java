package com.ulla.modules.business.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.business.mo.EoPointRulesMo;

public interface EoPointRulesMapper extends BaseMapper<EoPointRulesMo> {

    @Select("select * from biz_eo_point_rules where start > #{begin} and start <= #{after} and (delete_flag is null or delete_flag =0)")
    List<EoPointRulesMo> selectForAmount(BigDecimal begin, BigDecimal after);

}
