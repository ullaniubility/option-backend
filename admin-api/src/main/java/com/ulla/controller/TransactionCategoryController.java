package com.ulla.controller;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ulla.cache.Cache;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.TokenUtils;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.mo.TransactionCategoryMo;
import com.ulla.modules.business.qo.PairsQo;
import com.ulla.modules.business.qo.TransactionCategoryChildQo;
import com.ulla.modules.business.qo.TransactionCategoryQo;
import com.ulla.modules.business.service.TransactionCategoryChildService;
import com.ulla.modules.business.service.TransactionCategoryService;
import com.ulla.modules.business.vo.CascadePairVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "交易一级大类")
@Slf4j
@RestController
@RequestMapping("/transactionCategory")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionCategoryController {

    final TransactionCategoryService transactionCategoryService;

    final TransactionCategoryChildService transactionCategoryChildService;

    final Cache cache;

    @ApiOperation(value = "获取交易一级大类分页列表")
    @GetMapping("/getByPage")
    public ResultMessageVo<IPage<TransactionCategoryMo>> getByPage(TransactionCategoryQo qo) {
        return ResultUtil.data(transactionCategoryService.queryByParams(qo));
    }

    @ApiOperation(value = "获取交易一级大类列表")
    @GetMapping("/getList")
    public ResultMessageVo<List<TransactionCategoryMo>> getList(TransactionCategoryQo qo) {
        return ResultUtil.data(transactionCategoryService.getList(qo));
    }

    /**
     * 新增交易一级大类
     */
    @ApiOperation(value = "新增交易一级大类", notes = "新增交易一级大类")
    @PostMapping("/add")
    public ResultMessageVo add(@Validated @RequestBody TransactionCategoryMo mo, HttpServletRequest request) {
        try {
            mo.setCreateBy(TokenUtils.getLoginUser(request).getUid());
            return ResultUtil.data(transactionCategoryService.save(mo));
        } catch (Exception e) {
            log.info("新增交易一级大类出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    /**
     * 获取交易一级大类
     */
    @ApiOperation(value = "获取交易一级大类", notes = "获取交易一级大类")
    @GetMapping("/getById")
    public ResultMessageVo<TransactionCategoryMo> getById(@NotNull(message = "数据编号不能为空") Long id) {
        try {
            return ResultUtil.data(transactionCategoryService.getById(id));
        } catch (Exception e) {
            log.info("获取交易一级大类出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    /**
     * 编辑交易一级大类
     */
    @ApiOperation(value = "编辑交易一级大类", notes = "编辑交易一级大类")
    @PostMapping("/update")
    public ResultMessageVo update(@Validated @RequestBody TransactionCategoryMo mo) {
        try {
            if (null == mo.getId()) {
                return ResultUtil.error(4002, "The data number cannot be empty");
            }
            return ResultUtil.data(transactionCategoryService.updateById(mo));
        } catch (Exception e) {
            log.info("编辑交易一级大类出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    @ApiOperation(value = "获取交易二级类分页列表")
    @GetMapping("/getChildByPage")
    public ResultMessageVo<IPage<TransactionCategoryChildMo>> getChildByPage(TransactionCategoryChildQo qo) {
        return ResultUtil.data(transactionCategoryChildService.queryByParams(qo));
    }

    /**
     * 修改二级交易类的is_popular开关
     */
    @GetMapping("/updatePopular")
    @ApiOperation(value = "修改用户的活跃开关", notes = "通过uid修改用活跃开关")
    public ResultMessageVo updatePopular(Long id, Integer isPopular) {
        if (isPopular != 0 || isPopular != 1) {
            ResultUtil.error(4002, "Parameter abnormality");
        }
        UpdateWrapper<TransactionCategoryChildMo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("is_popular", isPopular);
        return ResultUtil.data(transactionCategoryChildService.update(updateWrapper));
    }

    /**
     * 
     * 二级交易类启用列表
     */
    @ApiOperation(value = "获取交易二级类列表")
    @GetMapping("/getChildList")
    public ResultMessageVo<List<TransactionCategoryChildMo>> getChildList(TransactionCategoryChildQo qo) {
        return ResultUtil.data(transactionCategoryChildService.getList(qo));
    }

    /**
     * 二级交易类所以数据列表
     */
    @ApiOperation(value = "获取交易二级类列表")
    @GetMapping("/allList")
    public ResultMessageVo<List<TransactionCategoryChildMo>> allList() {
        return ResultUtil.data(transactionCategoryChildService.list());
    }

    /**
     * 新增交易二级类
     */
    @ApiOperation(value = "新增交易二级类", notes = "新增交易二级类")
    @PostMapping("/addChild")
    public ResultMessageVo addChild(@Validated @RequestBody TransactionCategoryChildMo mo) {
        try {
            return ResultUtil.data(transactionCategoryChildService.save(mo));
        } catch (Exception e) {
            log.info("新增交易二级类出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    /**
     * 获取交易二级类
     */
    @ApiOperation(value = "获取交易二级类", notes = "获取交易二级类")
    @GetMapping("/getChildById")
    public ResultMessageVo<TransactionCategoryChildMo> getChildById(@NotNull(message = "数据编号不能为空") Long id) {
        try {
            return ResultUtil.data(transactionCategoryChildService.getById(id));
        } catch (Exception e) {
            log.info("获取交易二级类出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

    /**
     * 编辑交易二级类
     */
    @ApiOperation(value = "编辑交易二级类", notes = "编辑交易二级类")
    @PostMapping("/updateChild")
    public ResultMessageVo updateChild(@Validated @RequestBody TransactionCategoryChildMo mo) {
        try {
            if (null == mo.getId()) {
                return ResultUtil.error(4002, "The data number cannot be empty");
            }
            return ResultUtil.data(transactionCategoryChildService.updateById(mo));
        } catch (Exception e) {
            log.info("编辑交易二级类出错，", e);
            return ResultUtil.error(ResultCodeEnums.ERROR);
        }
    }

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
                    cascadePairVo.setParentId(pairsQo.getParentId());
                    return cascadePairVo;
                }).collect(Collectors.toList());
            return ResultUtil.data(cascadePairVos);
        }
    }

}
