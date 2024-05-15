package com.ulla.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 无需权限配置初始化
 * @since 2023/3/23 14:04
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "no-require")
public class NoRequireTokenUriConfiguration {

    private List<String> urls;

}
