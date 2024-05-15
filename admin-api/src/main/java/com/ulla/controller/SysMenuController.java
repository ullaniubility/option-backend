package com.ulla.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.SysMenuMo;
import com.ulla.modules.admin.service.SysMenuService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @Description {用户controller}
 * @author {clj}
 * @since {2023-4-8}
 */
@Api(value = "系统菜单配置", tags = {"系统菜单配置"})
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysMenuController {

    final SysMenuService sysMenuService;

    @ApiOperation("获取系统菜单")
    @GetMapping(value = "/getMenu")
    public ResultMessageVo getMenu(HttpServletRequest request) {
        return sysMenuService.getMenu();
    }

    @ApiOperation("获取启用状态的系统菜单")
    @GetMapping(value = "/getMenuForShow")
    public ResultMessageVo getMenuForShow(HttpServletRequest request) {
        return sysMenuService.getMenuForShow();
    }

    @ApiOperation("新增/编辑菜单")
    @PostMapping(value = "/saveMenu")
    public ResultMessageVo saveMenu(@RequestBody SysMenuMo sysMenuMo) {
        return sysMenuService.saveMenu(sysMenuMo);
    }

    @ApiOperation("更改菜单状态")
    @PostMapping(value = "/menuStatus")
    public ResultMessageVo menuStatus(@RequestBody SysMenuMo sysMenuMo) {
        return sysMenuService.menuStatus(sysMenuMo);
    }

    @ApiOperation("删除菜单")
    @PostMapping(value = "/deleteMenu")
    public ResultMessageVo deleteMenu(@RequestBody SysMenuMo sysMenuMo) {
        return sysMenuService.deleteMenu(sysMenuMo);
    }
}
