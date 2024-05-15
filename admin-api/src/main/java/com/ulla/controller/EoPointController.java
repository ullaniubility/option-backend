package com.ulla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.EoPointRulesMo;
import com.ulla.modules.business.service.EoPointForUserService;
import com.ulla.modules.business.service.EoPointRulesService;
import com.ulla.modules.business.vo.EoPointRulesVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(value = "eo积分", tags = {"eo积分"})
@Slf4j
@RestController
@RequestMapping("/eoPoint")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EoPointController {

    final EoPointRulesService eoPointRulesService;

    final EoPointForUserService eoPointForUserService;

    /**
     * 保存eo积分规则
     */
    @ApiOperation("保存eo积分规则")
    @PostMapping(value = "/saveOrUpdate")
    public ResultMessageVo saveRules(@RequestBody EoPointRulesMo mo) {
        return eoPointRulesService.saveRules(mo);
    }

    /**
     * eo积分规则分页
     */
    @ApiOperation("eo积分规则分页")
    @PostMapping(value = "/getPage")
    public ResultMessageVo getPage(@RequestBody EoPointRulesVo vo) {
        ResultMessageVo page = eoPointRulesService.getPage(vo);
        if (ObjectUtils.isEmpty(page.getResult())) {
            return null;
        }
        return page;
    }

    /**
     * 删除规则
     */
    @ApiOperation("删除")
    @PostMapping(value = "/delete")
    public ResultMessageVo delete(@RequestBody EoPointRulesMo mo) {
        return eoPointRulesService.delete(mo);
    }

    /**
     * 获取单独用户的eo积分
     */
    @ApiOperation("获取单独用户的eo积分")
    @GetMapping(value = "/getUserEoPoint")
    public ResultMessageVo getUserEoPoint(Long uid) {
        return eoPointForUserService.getOrCreate(uid);
    }
}
