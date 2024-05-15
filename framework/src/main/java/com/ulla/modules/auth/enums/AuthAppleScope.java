package com.ulla.modules.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Facebook 平台 OAuth 授权范围
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AuthAppleScope implements AuthScope {

    /**
     * {@code scope} 含义，以{@code description} 为准
     */
    EMAIL("email", "获取用户的邮箱", true), NAME("name", "获取用户的姓名", true);

    private final String scope;
    private final String description;
    private final boolean isDefault;

}
