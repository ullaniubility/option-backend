package com.ulla.modules.assets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.assets.mo.ActiveMo;
import com.ulla.modules.assets.mo.BalanceLogMo;
import com.ulla.modules.assets.vo.ActiveParameterVO;
import com.ulla.modules.assets.vo.BalanceLogParameterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 促销活动表 Mapper 接口
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Mapper
public interface ActiveMapper extends BaseMapper<ActiveMo> {

    /**
     * 根据时间和ID获取是否满足条件的促销活动
     * @param id 活动Id
     * @param time 接口请求时间
     * @return
     */
    public List<ActiveMo> getActiveListByParamer(@Param("id") Long id ,@Param("time") Long time );


    /**
     * 促销活动列表查询
     * @param vo 查询参数
     * @return
     */
    public List<ActiveMo> getActiveList(ActiveParameterVO vo);
    public Integer getActiveListCount(ActiveParameterVO vo);
}
