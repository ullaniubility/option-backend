package com.ulla.modules.business.qo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ulla.common.vo.PageVo;

/**
 * @author zhuyongdong
 * @Description 最近交易
 * @since 2023/3/20 17:43
 */
public class RecentTransactionsQo extends PageVo {

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", 0);
        return queryWrapper;
    }
}
