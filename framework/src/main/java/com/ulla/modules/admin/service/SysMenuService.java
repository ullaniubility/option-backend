package com.ulla.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.admin.mo.SysMenuMo;

/**
 * <p>
 * 后台系统菜单
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-16
 */
public interface SysMenuService extends IService<SysMenuMo> {
    ResultMessageVo getMenu();

    ResultMessageVo getMenuForShow();

    ResultMessageVo saveMenu(SysMenuMo sysMenuMo);

    ResultMessageVo menuStatus(SysMenuMo sysMenuMo);

    ResultMessageVo deleteMenu(SysMenuMo sysMenuMo);
}
