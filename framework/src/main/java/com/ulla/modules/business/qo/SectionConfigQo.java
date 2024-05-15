package com.ulla.modules.business.qo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ulla.common.vo.PageVo;

import lombok.Data;

/**
 * @Description 结算时间点配置查询参数
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
@Data
public class SectionConfigQo extends PageVo {

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", 0);
        return queryWrapper;
    }

}
