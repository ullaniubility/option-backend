package com.ulla.modules.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.admin.mo.SysMenuMo;

public interface SysMenuMapper extends BaseMapper<SysMenuMo> {

    @Select("select * from sys_menu where level = 1 and (delete_flag = 0 or delete_flag is null)")
    List<SysMenuMo> getParents();

    @Select("select * from sys_menu where parent_id is not null and (delete_flag = 0 or delete_flag is null)")
    List<SysMenuMo> getChildren();

    @Select("select * from sys_menu where level = 1 and status = 1 and (delete_flag = 0 or delete_flag is null)")
    List<SysMenuMo> getParentsForshow();

    @Select("select * from sys_menu where parent_id is not null and status = 1 and (delete_flag = 0 or delete_flag is null)")
    List<SysMenuMo> getChildrenForShow();
}
