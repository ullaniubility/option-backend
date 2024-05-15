package com.ulla.modules.assets.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.PageVo;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.mapper.DepositConfigHistoryMapper;
import com.ulla.modules.assets.mapper.DepositConfigMapper;
import com.ulla.modules.assets.mo.DepositConfigHistoryMo;
import com.ulla.modules.assets.mo.DepositConfigMo;
import com.ulla.modules.assets.service.DepositConfigService;
import com.ulla.modules.assets.vo.DepositConfigVO;
import com.ulla.mybatis.util.PageUtil;

/**
 * <p>
 * 入金配置表 服务实现类
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@Service
public class DepositConfigServiceImpl extends ServiceImpl<DepositConfigMapper, DepositConfigMo>
    implements DepositConfigService {

    @Autowired
    private DepositConfigMapper depositConfigMapper;

    @Autowired
    private DepositConfigHistoryMapper depositConfigHistoryMapper;

    /**
     * 入金配置 录入接口
     * 
     * @param mo
     * @return
     */
    public ResultMessageVo saveDepositConfig(DepositConfigMo mo) {
        DepositConfigHistoryMo history = new DepositConfigHistoryMo();
        // 获取入金配置金额
        Integer configAmount = mo.getConfigAmount();
        if (configAmount != null) {
            List<DepositConfigMo> depositConfigMos = depositConfigMapper.selectAmount(configAmount);
            if (CollectionUtils.isNotEmpty(depositConfigMos)) {
                return ResultUtil.error(500, "The deposit amount has been set, please do not repeat the setting!");
            }
            List<DepositConfigMo> mos = depositConfigMapper.selectSection(mo.getRangeBegin(), mo.getRangeEnd());
            if (CollectionUtils.isNotEmpty(mos)) {
                return ResultUtil.error(500,
                    "This interval has already been configured, please set it carefully, otherwise it may cause data confusion!");
            }
            List<DepositConfigMo> list = depositConfigMapper.selectSectionAll();
            for (DepositConfigMo mo1 : list) {
                if (mo.getRangeBegin() >= mo1.getRangeBegin() && mo.getRangeBegin() < mo1.getRangeEnd()) {
                    return ResultUtil.error(500,
                        "This interval has already been configured, please set it carefully, otherwise it may cause data confusion!");
                }
                if (mo.getRangeEnd() > mo1.getRangeBegin() && mo.getRangeEnd() < mo1.getRangeEnd()) {
                    return ResultUtil.error(500,
                        "This interval has already been configured, please set it carefully, otherwise it may cause data confusion!");
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        Long createTime = cal.getTimeInMillis();
        mo.setCreateTime(createTime);
        depositConfigMapper.insert(mo);
        BeanUtils.copyProperties(mo, history);
        history.setDepositId(mo.getId());
        history.setUpdateTime(createTime);
        history.setId(null);
        depositConfigHistoryMapper.insert(history);
        return ResultUtil.success();
    }

    /**
     * 入金配置 修改接口
     * 
     * @param mo
     * @return
     */
    public ResultMessageVo updateDepositConfig(DepositConfigMo mo) {
        DepositConfigHistoryMo history = new DepositConfigHistoryMo();
        String id = mo.getId() == null ? null : String.valueOf(mo.getId());
        DepositConfigMo config = new DepositConfigMo();
        // 获取历史配置信息，以便进行放入历史配置表中的操作
        if (StringUtils.isNotEmpty(id) && mo.getConfigAmount() != null) {
            List<DepositConfigMo> list = depositConfigMapper.getDepositConfigByAmount(mo.getId(), mo.getConfigAmount());
            // 如果入金配置不为空，则已存在该区间的入金配置，则返回错误提示
            if (CollectionUtils.isNotEmpty(list)) {
                return ResultUtil.error(500, "该入金金额已被设置，请勿重复设置！");
            }
            config = depositConfigMapper.selectById(id);
        }
        Calendar cal = Calendar.getInstance();
        Long time = cal.getTimeInMillis();
        mo.setUpdateTime(time);
        depositConfigMapper.updateById(mo);

        BeanUtils.copyProperties(config, history);
        history.setId(null);
        history.setCreateTime(time);
        history.setDepositId(config.getId());
        depositConfigHistoryMapper.insert(history);
        return ResultUtil.success();
    }

    /**
     * 入金配置 - 根据配置Id查询配置详情
     * 
     * @param id
     *            入金配置Id
     * @return
     */
    public ResultMessageVo getDepositConfigById(Long id) {
        DepositConfigMo config = depositConfigMapper.selectById(id);
        return ResultUtil.data(config);
    }

    /**
     * 入金配置 - 获取入金配置列表
     * 
     * @return
     */
    public ResultMessageVo<IPage<DepositConfigMo>> getDepositConfigList(PageVo page) {
        return ResultUtil.data(depositConfigMapper.getDepositConfigPage(PageUtil.initPage(page)));
    }

    /**
     * 入金配置 - 获取所有正常状态的入金配置
     * 
     * @return
     */
    public ResultMessageVo getNormalDepositConfigList() {
        List<DepositConfigVO> resultList = new ArrayList<>();
        // 取所有正常状态的入金配置
        List<DepositConfigMo> list = depositConfigMapper.getALLDepositConfigList();
        Calendar cal = Calendar.getInstance();
        Long time = cal.getTimeInMillis();
        for (DepositConfigMo mo : list) {
            DepositConfigVO vo = new DepositConfigVO();
            BeanUtils.copyProperties(mo, vo);
            vo.setRequestTime(time);
            resultList.add(vo);
        }

        return ResultUtil.data(resultList);
    }

}
