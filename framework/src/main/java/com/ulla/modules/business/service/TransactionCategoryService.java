package com.ulla.modules.business.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.business.mo.TransactionCategoryMo;
import com.ulla.modules.business.qo.TransactionCategoryQo;

/**
 * @author zhuyongdong
 * @Description 交易一级大类
 * @since 2023-02-27 22:50:31
 */
public interface TransactionCategoryService extends IService<TransactionCategoryMo> {

    /**
     * 交易一级大类分页查询列表
     *
     * @param transactionCategoryQo
     *            查询参数
     * @return 交易一级大类分页
     */
    IPage<TransactionCategoryMo> queryByParams(TransactionCategoryQo transactionCategoryQo);

    /**
     * 交易一级大类查询列表
     * 
     * @param transactionCategoryQo
     *            查询参数
     * @return 交易一级大类列表
     */
    List<TransactionCategoryMo> getList(TransactionCategoryQo transactionCategoryQo);
}
