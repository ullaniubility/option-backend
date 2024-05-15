package com.ulla.controller;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.mo.TransactionCategoryMo;
import com.ulla.modules.business.qo.PairsQo;
import com.ulla.modules.business.qo.TransactionCategoryQo;
import com.ulla.modules.business.service.TransactionCategoryChildService;
import com.ulla.modules.business.service.TransactionCategoryService;
import com.ulla.modules.business.vo.CascadePairVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @Description 交易对
 * @author zhuyongdong
 * @since 2023-04-17 11:58:04
 */
@Api(tags = "交易对")
@RestController
@RequestMapping("/symbol")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SymbolController {

    final Cache cache;

    final TransactionCategoryService transactionCategoryService;

    final TransactionCategoryChildService transactionCategoryChildService;

    @GetMapping("/list")
    @ApiOperation(value = "级联查询", notes = "级联查询")
    public ResultMessageVo quotationList(PairsQo pairsQo) {
        if (ObjectUtils.isEmpty(pairsQo.getParentId())) {
            TransactionCategoryQo categoryQo = new TransactionCategoryQo();
            categoryQo.setCategoryName(pairsQo.getName());
            categoryQo.setId(pairsQo.getId());
            List<TransactionCategoryMo> list = transactionCategoryService.getList(categoryQo);
            List<CascadePairVo> cascadePairVos = list.stream().map(k -> {
                CascadePairVo cascadePairVo = new CascadePairVo();
                cascadePairVo.setId(k.getId());
                cascadePairVo.setName(k.getCategoryName());
                return cascadePairVo;
            }).collect(Collectors.toList());
            return ResultUtil.data(cascadePairVos);
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<TransactionCategoryChildMo>>() {}.getType();
            List<TransactionCategoryChildMo> listChild =
                gson.fromJson(cache.get("binance:api:symbolList").toString(), type);
            List<CascadePairVo> cascadePairVos = listChild.stream()
                .filter(k -> k.getCategoryId().intValue() == pairsQo.getParentId().intValue()).map(v -> {
                    CascadePairVo cascadePairVo = new CascadePairVo();
                    cascadePairVo.setId(v.getId());
                    cascadePairVo.setName(v.getChildName());
                    cascadePairVo.setParentId(pairsQo.getId());
                    return cascadePairVo;
                }).collect(Collectors.toList());
            return ResultUtil.data(cascadePairVos);
        }
    }

}
