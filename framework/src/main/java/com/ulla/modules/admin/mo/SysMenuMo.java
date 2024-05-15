package com.ulla.modules.admin.mo;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenuMo extends BaseIdEntity {

    private String title;

    private Integer status;

    private String i18n;

    private String icon;

    private Long parentId;

    private String auth;

    private Integer level;

    private String redirect;

    private String path;

    private String component;

    private Integer sidebar;

    private String name;

    private Integer deleteFlag;

    private String link;

    @TableField(exist = false)
    private List<SysMenuMo> children;

    @TableField(exist = false)
    private SysMenuMo meta;

}
