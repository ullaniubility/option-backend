package com.ulla.modules.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.NumberConstant;
import com.ulla.modules.business.mapper.EoPointRulesMapper;
import com.ulla.modules.business.mo.EoPointRulesMo;
import com.ulla.modules.business.service.EoPointRulesService;
import com.ulla.modules.business.vo.EoPointRulesVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EoPointRulesServiceImpl extends ServiceImpl<EoPointRulesMapper, EoPointRulesMo>
    implements EoPointRulesService {

    final EoPointRulesMapper eoPointRulesMapper;

    @Override
    public ResultMessageVo delete(EoPointRulesMo mo) {
        try {
            if (mo.getId() == null) {
                return ResultUtil.error(4002, "Please pass the complete parameters");
            }
            EoPointRulesMo rulesMo = eoPointRulesMapper.selectById(mo.getId());
            if (rulesMo == null) {
                return ResultUtil.error(4002, "ID error");
            }
            rulesMo.setDeleteFlag(NumberConstant.ONE);
            eoPointRulesMapper.updateById(rulesMo);
            return ResultUtil.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(500, "System error");
        }
    }

    @Override
    public ResultMessageVo getPage(EoPointRulesVo vo) {
        try {
            Page<EoPointRulesMo> page = new Page<>(vo.getPage(), vo.getPageSize());
            QueryWrapper<EoPointRulesMo> wrapper = new QueryWrapper<>();
            wrapper.and(wp -> wp.eq("delete_flag", 0).or().isNull("delete_flag"));

            return ResultUtil.data(eoPointRulesMapper.selectPage(page, wrapper));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(500, "System error");
        }
    }

    @Override
    public ResultMessageVo saveRules(EoPointRulesMo mo) {
        try {
            if (mo.getStart() == null || mo.getEnd() == null || mo.getIsDisable() == null) {
                return ResultUtil.error(4002, "Please pass the completion parameters");
            }
            if (mo.getId() == null) {
                mo.setCreateTime(System.currentTimeMillis());
            }
            QueryWrapper<EoPointRulesMo> wrapper = new QueryWrapper<>();
            wrapper.and(wp -> wp.eq("delete_flag", 0).or().isNull("delete_flag"));
            if (mo.getId() != null) {
                wrapper.ne("id", mo.getId());
            }
            List<EoPointRulesMo> list = eoPointRulesMapper.selectList(wrapper);
            for (EoPointRulesMo rulesMo : list) {
                if ((mo.getEnd() < rulesMo.getEnd() && mo.getStart() > rulesMo.getStart())
                    || (mo.getStart() < rulesMo.getStart() && mo.getEnd() > rulesMo.getStart())
                    || (mo.getStart() < rulesMo.getEnd() && mo.getEnd() > rulesMo.getEnd())) {
                    return ResultUtil.error(4002, "The relevant interval configuration already exists in the system");
                }
            }
            this.saveOrUpdate(mo);
            return ResultUtil.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.error(500, "System error");
        }
    }
}
