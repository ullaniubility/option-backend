package com.ulla.cache.script;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @Description redis 脚本
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
@Configuration
public class LuaScript {

    /**
     * 库存扣减脚本
     */
    @Bean
    public DefaultRedisScript<Boolean> quantityScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/quantity.lua")));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }

    /**
     * 流量限制脚本
     * 
     * @return
     */
    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/limit.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
