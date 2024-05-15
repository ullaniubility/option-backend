package com.ulla.modules.business.service.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.utils.StringUtils;
import com.ulla.modules.business.mapper.TransactionCategoryChildMapper;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.qo.TransactionCategoryChildQo;
import com.ulla.modules.business.service.TransactionCategoryChildService;
import com.ulla.mybatis.util.PageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionCategoryChildServiceImpl extends
    ServiceImpl<TransactionCategoryChildMapper, TransactionCategoryChildMo> implements TransactionCategoryChildService {

    final TransactionCategoryChildMapper transactionCategoryChildMapper;

    final Cache cache;

    @Override
    public IPage<TransactionCategoryChildMo> queryByParams(TransactionCategoryChildQo transactionCategoryChildQo) {
        return this.page(PageUtil.initPage(transactionCategoryChildQo), transactionCategoryChildQo.queryWrapper());
    }

    @Override
    public List<TransactionCategoryChildMo> getList(TransactionCategoryChildQo transactionCategoryQo) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<TransactionCategoryChildMo>>() {}.getType();
        List<TransactionCategoryChildMo> list = gson.fromJson(cache.get("binance:api:symbolList").toString(), type);
        if (transactionCategoryQo.getCategoryId() != null) {
            list = list.stream()
                .filter(v -> v.getCategoryId().longValue() == transactionCategoryQo.getCategoryId().longValue())
                .collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(transactionCategoryQo.getChildName())) {
            list = list.stream().filter(v -> v.getChildName().contains(transactionCategoryQo.getChildName()))
                .collect(Collectors.toList());
        }
        return list;
    }

}
