package com.ulla.common.utils;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/4/19 10:28
 */
public class UserUtil {

    private static ThreadLocal<CommonHeader> current = new ThreadLocal<>();

    /**
     * 获取静态的ThreadLocal对象
     * 
     * @return
     */
    public static ThreadLocal<CommonHeader> getCurrent() {
        return current;
    }

    /**
     * 获取ip
     * 
     * @return
     */
    public static String getIp() {
        CommonHeader request = current.get();
        if (request == null) {
            return StringUtils.EMPTY;
        }
        return request.getIp();
    }

    /**
     * 获取uid
     * 
     * @return
     */
    public static Long getUid() {
        CommonHeader request = current.get();
        if (request == null) {
            return null;
        }
        return request.getUid();
    }

    /**
     * 获取openId
     *
     * @return
     */
    public static String getOpenId() {
        CommonHeader request = current.get();
        if (request == null) {
            return null;
        }
        return request.getOpenId();
    }

    /**
     * 获取token
     *
     * @return
     */
    public static String getToken() {
        CommonHeader request = current.get();
        if (request == null) {
            return null;
        }
        return request.getToken();
    }

    /**
     * 获取指纹
     *
     * @return
     */
    public static String getFingerprint() {
        CommonHeader request = current.get();
        if (request == null) {
            return null;
        }
        return request.getFingerprint();
    }

    /**
     * 获取会员等级
     *
     * @return
     */
    public static String getUserLevel() {
        CommonHeader request = current.get();
        if (request == null) {
            return StringUtils.EMPTY;
        }
        return request.getUserLevel();
    }

    /**
     * 获取封装对象
     * 
     * @return
     */
    public static CommonHeader getCommonReq() {
        CommonHeader request = current.get();
        if (request == null) {
            return new CommonHeader(StringUtils.EMPTY, 0L, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                StringUtils.EMPTY);
        }
        return request;
    }

}
