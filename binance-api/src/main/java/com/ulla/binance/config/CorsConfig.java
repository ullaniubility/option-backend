package com.ulla.binance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhuyongdong
 * @Description 后端跨域
 * @since 2023/2/9 10:59
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口
            .allowCredentials(true) // 是否发送 Cookie
            .allowedOriginPatterns("*") // 支持域
            .allowedMethods("GET", "POST", "PUT", "DELETE") // 支持方法
            .allowedHeaders("*").exposedHeaders("*");
    }

}
