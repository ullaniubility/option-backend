package com.ulla.common.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 从header中获取设备指纹*
 */
public class FingerprintUtil {

    /**
     *
     * 获取设备指纹
     * 
     * @author clj
     * @param request
     *            HttpServletRequest
     * @return 登录信息
     */
    public static String getFingerprint(HttpServletRequest request) {
        String fingerprint;
        fingerprint = request.getHeader("Murmur");
        return fingerprint;
    }
}
