package com.ulla.modules.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ulla.cache.impl.RedisCache;
import com.ulla.modules.auth.enums.AuthCommonSource;
import com.ulla.modules.auth.mapper.ThirdConfigMapper;
import com.ulla.modules.auth.mo.ThirdConfigMo;
import com.ulla.modules.auth.service.ThirdConfigService;

import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 1
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThirdConfigServiceImpl extends ServiceImpl<ThirdConfigMapper, ThirdConfigMo>
    implements ThirdConfigService {

    final RedisCache redisCache;

    @Override
    public ThirdConfigMo getConfigInCacheByName(AuthCommonSource source) {
        ThirdConfigMo thirdConfigMo = null;
        if (ObjectUtil.isEmpty(source)) {
            throw new RuntimeException("获取第三方登录配置失败");
        }
        try {
            thirdConfigMo = (ThirdConfigMo)redisCache.get(source.cachePrefix());
        } catch (Exception e) {
            log.error("获取 :{}第三方登录信息缓存失败，查询数据库", source.getName());
            log.error(e.getMessage());
        }
        if (ObjectUtil.isEmpty(thirdConfigMo)) {
            thirdConfigMo =
                getOne(new LambdaQueryWrapper<ThirdConfigMo>().eq(ThirdConfigMo::getName, source.getName()));
            if (ObjectUtil.isNotEmpty(thirdConfigMo)) {
                redisCache.put(source.cachePrefix(), thirdConfigMo);
            } else {
                throw new RuntimeException("获取第三方登录配置失败");
            }
        }
        return thirdConfigMo;
    }

    @Override
    public void cleanCache(AuthCommonSource source) {
        if (ObjectUtil.isEmpty(source)) {
            throw new RuntimeException("获取第三方登录配置失败");
        }
        try {
            redisCache.remove(source.cachePrefix());
        } catch (Exception e) {
            log.error("清理 :{}第三方登录信息缓存失败", source.getName());
            log.error(e.getMessage());
        }
    }
}
