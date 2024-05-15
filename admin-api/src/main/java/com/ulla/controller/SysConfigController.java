package com.ulla.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ulla.common.utils.DecryptStringUtil;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.NewUserAmountMo;
import com.ulla.modules.admin.mo.SysConfigMo;
import com.ulla.modules.admin.qo.SysConfigQo;
import com.ulla.modules.admin.service.SysConfigService;
import com.ulla.modules.auth.service.UserLevelService;
import com.ulla.modules.business.vo.MemberPopoverVo;
import com.ulla.modules.business.vo.MoneyPopoverVo;
import com.ulla.modules.business.vo.WithdrawalVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.net.URLDecoder;
import lombok.RequiredArgsConstructor;

/**
 * @Description {用户controller}
 * @author {clj}
 * @since {2023-4-8}
 */
@Api(value = "系统配置", tags = {"系统配置"})
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysConfigController {

    final SysConfigService sysConfigService;

    final UserLevelService userLevelService;

    @ApiOperation("获取系统菜单")
    @PostMapping(value = "/pageList")
    public ResultMessageVo pageList(@RequestBody SysConfigQo qo) {
        Page<SysConfigMo> page = new Page<>(qo.getPage(), qo.getPageSize());
        ResultMessageVo resultMessageVo = sysConfigService.pageList(page, qo);
        if (ObjectUtils.isEmpty(resultMessageVo.getResult())) {
            return null;
        }
        return resultMessageVo;
    }

    @ApiOperation("新增/编辑菜单")
    @PostMapping(value = "/saveConfig")
    public ResultMessageVo saveConfig(@RequestBody SysConfigMo sysConfigMo) {
        return sysConfigService.saveConfig(sysConfigMo);
    }

    @ApiOperation("查看提现配置")
    @GetMapping(value = "/getWithdrawal")
    public ResultMessageVo getWithdrawal() {
        return sysConfigService.getWithdrawal();
    }

    @ApiOperation("编辑提现配置")
    @PostMapping(value = "/updateWithdrawal")
    public ResultMessageVo updateWithdrawal(@RequestBody WithdrawalVo withdrawalVo) {
        return sysConfigService.updateWithdrawal(withdrawalVo, UserUtil.getUid());
    }

    /**
     * 二期弹窗配置列表
     */
    @ApiOperation("弹窗系统菜单")
    @GetMapping(value = "/popoverList")
    public ResultMessageVo popoverList() {
        List<SysConfigMo> list =
            sysConfigService.list(Wrappers.<SysConfigMo>query().lambda().eq(SysConfigMo::getConfigType, "NEW"));
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            // list = sysConfigService.popoverList();
        }
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysConfigMo entity : list) {
                String key = entity.getConfigKey();
                String value = entity.getConfigValue();
                if (!map.containsKey(key)) {
                    map.put(key, JSONArray.parse(value));
                }
            }
        }
        return ResultUtil.data(map);
    }

    /**
     * 新用户弹出框配置编辑
     */
    @ApiOperation("新增/弹出框配置编辑")
    @PostMapping(value = "/savePopover")
    public ResultMessageVo savePopover(@RequestBody MemberPopoverVo member) {
        UpdateWrapper<SysConfigMo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("config_key", "MEMBER_CONFIG").set("config_value", JSON.toJSONString(member));
        sysConfigService.update(updateWrapper);
        return ResultUtil.success(200, "保存成功");
    }

    /**
     * 入金弹出框配置编辑
     */
    @ApiOperation("新增/弹出框配置编辑")
    @PostMapping(value = "/saveMoney")
    public ResultMessageVo saveMoney(@RequestBody MoneyPopoverVo money) {
        UpdateWrapper<SysConfigMo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("config_key", "MONEY_PAY").set("config_value", JSON.toJSONString(money));
        sysConfigService.update(updateWrapper);
        return ResultUtil.success(200, "保存成功");
    }

    /**
     * 后台密文解密
     */
    @ApiOperation("密文解密")
    @PostMapping(value = "/decryptString")
    public ResultMessageVo decryptString(@RequestBody JSONObject jsonObject) {
        String ciphertext = jsonObject.getString("ciphertext");
        return ResultUtil.data(URLDecoder.decode(DecryptStringUtil.getDecryptString(ciphertext)));
    }

    /**
     * 会员等级
     */
    @ApiOperation("会员等级")
    @GetMapping(value = "/userLevelList")
    public ResultMessageVo userLevelList() {
        return userLevelService.getList();
    }

    /**
     * 获取新用户模拟余额配置
     */
    @ApiOperation("获取新用户模拟余额配置")
    @GetMapping(value = "/getNewUserAmountConfig")
    public ResultMessageVo getNewUserAmountConfig() {
        QueryWrapper<SysConfigMo> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("config_key", "NEW_USER_AMOUNT");
        return ResultUtil.data(
            JSONObject.parseObject(sysConfigService.getOne(updateWrapper).getConfigValue(), NewUserAmountMo.class));
    }

    /**
     * 修改新用户模拟余额配置
     */
    @ApiOperation("修改新用户模拟余额配置")
    @PostMapping(value = "/updateNewUserAmountConfig")
    public ResultMessageVo updateNewUserAmountConfig(@RequestBody NewUserAmountMo userAmountMo) {
        UpdateWrapper<SysConfigMo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("config_key", "NEW_USER_AMOUNT").set("config_value", JSON.toJSONString(userAmountMo));
        sysConfigService.update(updateWrapper);
        return ResultUtil.success(200, "保存成功");
    }

}
