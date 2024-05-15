package com.ulla.modules.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.common.utils.BeanUtil;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.StringUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.constant.NumberConstant;
import com.ulla.modules.admin.mapper.SysMenuMapper;
import com.ulla.modules.admin.mo.SysMenuMo;
import com.ulla.modules.admin.service.SysMenuService;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 后台系统菜单
 * </p>
 *
 * @author jetBrains
 * @since 2023-03-16
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuMo> implements SysMenuService {

    final SysMenuMapper sysMenuMapper;

    @Override
    public ResultMessageVo getMenu() {
        List<SysMenuMo> parents = sysMenuMapper.getParents();
        List<SysMenuMo> children = sysMenuMapper.getChildren();
        return getResultMessageVo(parents, children);
    }

    @Override
    public ResultMessageVo getMenuForShow() {
        List<SysMenuMo> parents = sysMenuMapper.getParentsForshow();
        List<SysMenuMo> children = sysMenuMapper.getChildrenForShow();
        return getResultMessageVo(parents, children);
    }

    @Override
    public ResultMessageVo saveMenu(SysMenuMo sysMenuMo) {
        if (StringUtils.isEmpty(sysMenuMo.getTitle()) || StringUtils.isEmpty(sysMenuMo.getAuth())
            || StringUtils.isEmpty(sysMenuMo.getPath()) || sysMenuMo.getLevel() == null
            || sysMenuMo.getSidebar() == null) {
            return ResultUtil.error(4002, "Parameter is empty");
        }
        if (sysMenuMo.getId() == null) {
            return ResultUtil.data(this.save(sysMenuMo));
        } else {
            return ResultUtil.data(sysMenuMapper.updateById(sysMenuMo));
        }
    }

    @Override
    public ResultMessageVo menuStatus(SysMenuMo sysMenuMo) {
        if (sysMenuMo.getId() == null || sysMenuMo.getStatus() == null) {
            return ResultUtil.error(4002, "Parameter is empty");
        }
        SysMenuMo menuMo = sysMenuMapper.selectById(sysMenuMo.getId());
        menuMo.setStatus(sysMenuMo.getStatus());
        sysMenuMapper.updateById(menuMo);
        return ResultUtil.data(sysMenuMapper.updateById(menuMo));
    }

    @Override
    public ResultMessageVo deleteMenu(SysMenuMo sysMenuMo) {
        if (sysMenuMo.getId() == null) {
            return ResultUtil.error(4002, "Parameter is empty");
        }
        SysMenuMo mo = sysMenuMapper.selectById(sysMenuMo.getId());
        mo.setDeleteFlag(NumberConstant.ONE);
        sysMenuMapper.updateById(mo);
        return ResultUtil.success(NumberConstant.T200T, "操作成功");
    }

    private ResultMessageVo getResultMessageVo(List<SysMenuMo> parents, List<SysMenuMo> children) {
        for (SysMenuMo parent : parents) {
            if (parent.getMeta() == null) {
                SysMenuMo mo = new SysMenuMo();
                BeanUtil.copyProperties(parent, mo);
                parent.setMeta(mo);
                // 预防循环设置
                parent.getMeta().setMeta(null);
            }
            List<SysMenuMo> childList = new ArrayList<>();
            for (SysMenuMo child : children) {
                if (child.getMeta() == null) {
                    SysMenuMo mo1 = new SysMenuMo();
                    BeanUtil.copyProperties(parent, mo1);
                    child.setMeta(mo1);
                    child.getMeta().setMeta(null);
                }
                if (child.getParentId().equals(parent.getId())) {
                    childList.add(child);
                }
                List<SysMenuMo> childList2 = new ArrayList<>();
                for (SysMenuMo child2 : children) {
                    if (child.getId().equals(child2.getParentId())) {
                        childList2.add(child2);
                    }
                }
                child.setChildren(childList2);
            }
            parent.setChildren(childList);
        }
        return ResultUtil.data(parents);
    }
}
