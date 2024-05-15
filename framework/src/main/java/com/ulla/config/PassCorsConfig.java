package com.ulla.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

/**
 * @author zhuyongdong
 * @Description 后端跨域
 * @since 2023/2/9 10:59
 */
@Configuration
public class PassCorsConfig implements WebMvcConfigurer {

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*"); // 设置访问源请求头
        corsConfiguration.addAllowedMethod("GET"); // 允许GET方法访问
        corsConfiguration.addAllowedMethod("POST"); // 允许POST方法访问
        corsConfiguration.addAllowedMethod("OPTIONS"); // 允许OPTIONS方法访问
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        // 2.4以后这样设置 替换 addAllowedOrigin("*)
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList(CorsConfiguration.ALL));
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4 对接口配置跨域设置
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptor) {
        interceptor.addInterceptor(new BaseInterceptor()).addPathPatterns("/**");
    }

}
