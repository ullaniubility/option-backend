package com.ulla.modules.assets.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mo.DepositConfigMo;

/**
 * <p>
 * 入金配置表 服务类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
public interface DepositConfigService extends IService<DepositConfigMo> {

    /**
     * 入金配置 录入接口
     * 
     * @param mo
     * @return
     */
    public ResultMessageVo saveDepositConfig(DepositConfigMo mo);

    /**
     * 入金配置 修改接口
     * 
     * @param mo
     * @return
     */
    public ResultMessageVo updateDepositConfig(DepositConfigMo mo);

    /**
     * 入金配置 - 根据配置Id查询配置详情
     * 
     * @param id
     *            入金配置Id
     * @return
     */
    public ResultMessageVo getDepositConfigById(Long id);

    /**
     * 入金配置 - 获取入金配置列表
     * 
     * @return
     */
    public ResultMessageVo<IPage<DepositConfigMo>> getDepositConfigList(PageVo page);

    /**
     * 入金配置 - 获取所有正常状态的入金配置
     * 
     * @return
     */
    public ResultMessageVo getNormalDepositConfigList();
}
