package com.ulla.modules.assets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.assets.mo.DepositConfigHistoryMo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 入金配置表 Mapper 接口
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Mapper
public interface DepositConfigHistoryMapper extends BaseMapper<DepositConfigHistoryMo> {

    /**
     * 获取最新的配置
     * @param amount
     * @return
     */
    List<DepositConfigHistoryMo> getAllDepositConfig(@Param("amount") Integer amount);


    /**
     * 获取最新的配置 - 新写
     * @param amount
     * @return
     */
    List<DepositConfigHistoryMo> getLatestConfiguration(@Param("amount") Integer amount);
}
