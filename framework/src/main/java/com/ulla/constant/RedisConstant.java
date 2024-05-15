package com.ulla.constant;

import com.ulla.common.utils.DateUtil;

/**
 * 缓存常量key
 * 
 * @author 1
 */
public class RedisConstant {

    /**
     * 缓存分隔符
     */
    public final static String SPLIT = ":";

    /**
     * 缓存公共前缀 可以根据业务模块新增一个命名
     */
    public final static String PREFIX = "ulla_option" + SPLIT;

    public static class ThirdConfig {
        public final static String THIRD_CONFIG_PREFIX = PREFIX + "third_config" + SPLIT;
        public final static String FACEBOOK = THIRD_CONFIG_PREFIX + "facebook";
        public final static String GOOGLE = THIRD_CONFIG_PREFIX + "google";
        public final static String APPLE = THIRD_CONFIG_PREFIX + "apple";
    }

    public static class User {

        static Long dateTime = DateUtil.getTodayStartTime();
        public final static String USER_ONLINE_NUM = PREFIX + "ONLINE_NUM";
        public final static String VIRTUALLY_USER_ONLINE_NUM = PREFIX + "VIRTUALLY_ONLINE_NUM" + SPLIT + dateTime;
    }

    public static class Order {
        static Long dateTime = DateUtil.getTodayStartTime();

        public final static String ORDER_RANK = PREFIX + "ORDER_RANK" + SPLIT;
        public final static String VIRTUALLY_ORDER_TOTAL_AMOUNT = PREFIX + "OrderTotalAmount" + SPLIT + dateTime;
        public final static String VIRTUALLY_ORDER_COUNT = PREFIX + "todayOrderCount" + SPLIT + dateTime;
    }

}
