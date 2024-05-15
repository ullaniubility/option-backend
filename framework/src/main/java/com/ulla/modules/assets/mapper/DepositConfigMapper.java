package com.ulla.modules.assets.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.modules.assets.mo.DepositConfigMo;
import com.ulla.modules.auth.vo.UserPageVo;

/**
 * <p>
 * 入金配置表 Mapper 接口
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Mapper
public interface DepositConfigMapper extends BaseMapper<DepositConfigMo> {

    /**
     * 按入金金额筛选入金配置
     *
     * @param configAmount
     *            配置金额
     * @return
     */
    public List<DepositConfigMo> getDepositConfig(@Param("configAmount") Integer configAmount);

    /**
     * 按ID和入金金额筛选入金配置
     *
     * @param configAmount
     *            配置金额
     * @return
     */
    public List<DepositConfigMo> getDepositConfigByAmount(@Param("id") Long id,
        @Param("configAmount") Integer configAmount);

    /**
     * 入金配置列表查询
     *
     * @return
     */
    @Select("SELECT id, config_amount, range_begin, range_end, reward_model, reward_amount, button_background_color, "
        + "button_border_color, newcomer_active_flag, active_time, active_reward_model, active_reward_amount, state, "
        + "remark, create_by, create_time, update_by, update_time, delete_flag FROM sys_deposit_config WHERE delete_flag = 0")
    IPage<DepositConfigMo> getDepositConfigPage(Page<UserPageVo> page);

    public Integer getDepositConfigListCount();

    /**
     * 入金配置 - 获取所有正常状态的入金配置
     *
     * @return
     */
    public List<DepositConfigMo> getALLDepositConfigList();

    @Select("SELECT config_amount,range_begin,range_end FROM sys_deposit_config WHERE config_amount=#{configAmount} AND delete_flag = 0")
    public List<DepositConfigMo> selectAmount(Integer configAmount);

    @Select("SELECT config_amount,range_begin,range_end FROM sys_deposit_config WHERE  range_begin <=#{rangeBegin} AND range_end>=#{rangeEnd} AND delete_flag = 0")
    public List<DepositConfigMo> selectSection(Integer rangeBegin, Integer rangeEnd);

    @Select("SELECT range_begin,range_end FROM sys_deposit_config WHERE delete_flag = 0")
    public List<DepositConfigMo> selectSectionAll();
}
