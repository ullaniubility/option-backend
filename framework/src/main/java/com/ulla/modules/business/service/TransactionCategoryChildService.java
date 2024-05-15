package com.ulla.modules.business.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.qo.TransactionCategoryChildQo;

/**
 * @Description 交易类
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
public interface TransactionCategoryChildService extends IService<TransactionCategoryChildMo> {
    IPage<TransactionCategoryChildMo> queryByParams(TransactionCategoryChildQo transactionCategoryChildQo);

    List<TransactionCategoryChildMo> getList(TransactionCategoryChildQo transactionCategoryChildQo);
}
