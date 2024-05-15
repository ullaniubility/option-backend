package com.ulla.cache.limit.interceptor;

import java.io.Serializable;

import com.ulla.cache.limit.enums.LimitTypeEnums;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.utils.IpUtils;
import com.ulla.common.vo.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.ImmutableList;
import com.ulla.cache.limit.annotation.LimitPoint;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 流量拦截
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
@Aspect
@Configuration
@Slf4j
public class LimitInterceptor {
    private RedisTemplate<String, Serializable> redisTemplate;

    private DefaultRedisScript<Long> limitScript;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setLimitScript(DefaultRedisScript<Long> limitScript) {
        this.limitScript = limitScript;
    }

    @Before("@annotation(limitPointAnnotation)")
    public void interceptor(LimitPoint limitPointAnnotation) {
        LimitTypeEnums limitTypeEnums = limitPointAnnotation.limitType();

        String key;
        int limitPeriod = limitPointAnnotation.period();
        int limitCount = limitPointAnnotation.limit();
        if (limitTypeEnums == LimitTypeEnums.CUSTOMER) {
            key = limitPointAnnotation.key();
        } else {
            key = limitPointAnnotation.key() + IpUtils
                .getIpAddress(((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest());
        }
        ImmutableList<String> keys = ImmutableList.of(StringUtils.join(limitPointAnnotation.prefix(), key));
        try {
            Number count = redisTemplate.execute(limitScript, keys, limitCount, limitPeriod);
            assert count != null;
            log.info("限制请求{}, 当前请求{},缓存key{}", limitCount, count.intValue(), key);
            // 如果缓存里没有值，或者他的值小于限制频率
            if (count.intValue() > limitCount) {
                throw new ServiceException(ResultCodeEnums.LIMIT_ERROR);
            }
        }
        // 如果从redis中执行都值判定为空，则这里跳过
        catch (NullPointerException e) {
            return;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("限流异常", e);
            throw new ServiceException(ResultCodeEnums.ERROR);
        }
    }

}