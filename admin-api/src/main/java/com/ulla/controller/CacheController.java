package com.ulla.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ulla.cache.Cache;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.utils.UserUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.UserLevelMapper;
import com.ulla.modules.auth.mo.UserLevelMo;
import com.ulla.modules.business.mapper.TransactionCategoryChildMapper;
import com.ulla.modules.business.mapper.TransactionConfigMapper;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.mo.TransactionConfigMo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 后台缓存管理
 * @author zhuyongdong
 * @since 2023-04-17 10:51:42
 */
@Api(value = "后台缓存管理", tags = {"后台管理"})
@Slf4j
@RestController
@RequestMapping("/sysCache")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CacheController {

    final Cache cache;

    final TransactionCategoryChildMapper childMapper;

    final UserLevelMapper userLevelMapper;

    final TransactionConfigMapper transactionConfigMapper;

    @ApiOperation("删除所有缓存")
    @GetMapping(value = "/deleteAll")
    public ResultMessageVo deleteAll() {
        try {
            log.info(">>>>>>>>>>>>>>>开始清理缓存<<<<<<<<<<<<<<<<<");
            List<Object> keys = cache.keys("*");
            cache.multiDel(keys);
            log.info(">>>>>>>>>>>>>>>清理缓存结束，共清理{}条缓存。<<<<<<<<<<<<<<<<<", keys.size());
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            return ResultUtil.success();
        } catch (Exception e) {
            log.info(">>>>>>>>>>>>>>>清理缓存失败<<<<<<<<<<<<<<<<<");
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            log.error(e.getMessage());
            return ResultUtil.error(ResultCodeEnums.SYSTEM_CACHE_DELETE_ALL_ERROR);
        }
    }

    @ApiOperation("根据key删除缓存")
    @GetMapping(value = "/deleteCacheByKey")
    public ResultMessageVo deleteCacheByKey(String key) {
        try {
            cache.remove(key);
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            return ResultUtil.success();
        } catch (Exception e) {
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            log.error(e.getMessage());
            return ResultUtil.error(ResultCodeEnums.SYSTEM_CACHE_DELETE_ERROR);
        }
    }

    /**
     * @Param sortRule 排序规则 1.首字母缩写正序 2.首字母缩写倒序 3.编号排序正序 4.编号排序倒序 5.盈利百分化
     */
    @ApiOperation("刷新交易对缓存")
    @GetMapping(value = "/refreshSymbolListCache")
    public ResultMessageVo refreshSymbolListCache(Integer sortRule) {
        try {
            if (null == sortRule) {
                sortRule = 5;
            }
            LambdaQueryWrapper<TransactionCategoryChildMo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TransactionCategoryChildMo::getDeleteFlag, 0);
            wrapper.eq(TransactionCategoryChildMo::getUsingFlag, 0);
            wrapper.eq(TransactionCategoryChildMo::getStatusFlag, 0);
            switch (sortRule) {
                case 1: {
                    wrapper.orderByDesc(TransactionCategoryChildMo::getIsPopular)
                        .orderByAsc(TransactionCategoryChildMo::getChildName);
                    break;
                }
                case 2: {
                    wrapper.orderByDesc(TransactionCategoryChildMo::getIsPopular)
                        .orderByDesc(TransactionCategoryChildMo::getChildName);
                    break;
                }
                case 3: {
                    wrapper.orderByDesc(TransactionCategoryChildMo::getIsPopular)
                        .orderByAsc(TransactionCategoryChildMo::getSortNumber);
                    break;
                }
                case 4: {
                    wrapper.orderByDesc(TransactionCategoryChildMo::getIsPopular)
                        .orderByDesc(TransactionCategoryChildMo::getSortNumber);
                    break;
                }
                default: {
                    wrapper.orderByDesc(TransactionCategoryChildMo::getIsPopular)
                        .orderByDesc(TransactionCategoryChildMo::getProfitPercent);
                }
            }
            List<TransactionCategoryChildMo> list = childMapper.selectList(wrapper);
            cache.put("binance:api:symbolList", list);
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            return ResultUtil.success();
        } catch (Exception e) {
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            log.error(e.getMessage());
            return ResultUtil.error(ResultCodeEnums.REFRESH_SYMBOL_LIST_ERROR);
        }
    }

    @Component
    @Order(value = 2)
    public class StartRunnerOne implements CommandLineRunner {
        @Override
        public void run(String... args) {
            log.info(">>>服务启动完成，执行缓存初始化任务<<<<");
            refreshSymbolListCache(5);
            refreshUserLevel();
            refreshClosePosition();
            log.info(">>>缓存初始化完毕<<<<");
        }
    }

    @ApiOperation("刷新会员等级缓存")
    @GetMapping(value = "/refreshUserLevel")
    public ResultMessageVo refreshUserLevel() {
        try {
            LambdaQueryWrapper<UserLevelMo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserLevelMo::getOffFlag, 0);
            wrapper.orderByAsc(UserLevelMo::getSortNum);
            List<UserLevelMo> list = userLevelMapper.selectList(wrapper);
            cache.put("user:level:list", list);
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            return ResultUtil.success();
        } catch (Exception e) {
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            log.error(e.getMessage());
            return ResultUtil.error(ResultCodeEnums.REFRESH_USER_LEVEL_LIST_ERROR);
        }
    }

    @ApiOperation("刷新平仓缓存")
    @GetMapping(value = "/refreshClosePosition")
    public ResultMessageVo refreshClosePosition() {
        try {
            TransactionConfigMo transactionConfigMo = transactionConfigMapper.selectById(1);
            String max = new BigDecimal(transactionConfigMo.getLossRatio().toString())
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
            String min = new BigDecimal(transactionConfigMo.getWithdrawal().toString())
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
            Map<String, String> map = new HashMap<>();
            map.put("closePositionMax", max);
            map.put("closePositionMin", min);
            cache.put("user:closePosition:map", map);
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            return ResultUtil.success();
        } catch (Exception e) {
            log.info(">>>>>>>>>>>>>>>操作管理uid:{}<<<<<<<<<<<<<<<<<", UserUtil.getUid());
            log.error(e.getMessage());
            return ResultUtil.error(ResultCodeEnums.REFRESH_CLOSE_POSITION_ERROR);
        }
    }

}
