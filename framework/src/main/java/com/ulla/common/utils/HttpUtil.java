package com.ulla.common.utils;

import javax.servlet.http.HttpServletRequest;

import com.ulla.constant.UserConstant;
import com.ulla.modules.auth.mo.LoginUserMo;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/4/19 10:34
 */
public class HttpUtil {

    /**
     * 获取请求头信息
     *
     * @param request
     * @return
     */
    public static CommonHeader getCommonHeader(HttpServletRequest request) {
        String token = request.getHeader(UserConstant.TOKEN);
        if (StringUtils.isNotBlank(token)) {
            LoginUserMo loginUserMo = TokenUtils.getLoginUser(token);
            return new CommonHeader(HttpUtil.getIp(request), loginUserMo.getUid(), loginUserMo.getOpenId(), token,
                request.getHeader(UserConstant.MURMUR), loginUserMo.getUserLevel());
        }
        return new CommonHeader(HttpUtil.getIp(request), null, null, token, request.getHeader(UserConstant.MURMUR),
            null);
    }

    /**
     * 获取IP
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}
