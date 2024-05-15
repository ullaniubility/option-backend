package com.ulla.modules.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.modules.business.mapper.TransactionCategoryMapper;
import com.ulla.modules.business.mo.TransactionCategoryMo;
import com.ulla.modules.business.qo.TransactionCategoryQo;
import com.ulla.modules.business.service.TransactionCategoryService;
import com.ulla.mybatis.util.PageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionCategoryServiceImpl extends ServiceImpl<TransactionCategoryMapper, TransactionCategoryMo>
    implements TransactionCategoryService {

    final TransactionCategoryMapper transactionCategoryMapper;

    @Override
    public IPage<TransactionCategoryMo> queryByParams(TransactionCategoryQo transactionCategoryQo) {
        return this.page(PageUtil.initPage(transactionCategoryQo), transactionCategoryQo.queryWrapper());
    }

    @Override
    public List<TransactionCategoryMo> getList(TransactionCategoryQo qo) {
        return transactionCategoryMapper.selectList(qo.queryWrapper());
    }
}
