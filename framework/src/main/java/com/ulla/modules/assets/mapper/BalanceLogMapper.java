package com.ulla.modules.assets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.assets.mo.AddressMo;
import com.ulla.modules.assets.mo.BalanceLogMo;
import com.ulla.modules.assets.vo.AddressParameterVO;
import com.ulla.modules.assets.vo.BalanceLogParameterVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户资产流水表 Mapper 接口
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Mapper
public interface BalanceLogMapper extends BaseMapper<BalanceLogMo> {


    /**
     * 资金流水表列表查询
     * @param vo 查询参数
     * @return
     */
    public List<BalanceLogMo> getBalanceLogList(BalanceLogParameterVO vo);
    public Integer getBalanceLogListCount(BalanceLogParameterVO vo);

}
