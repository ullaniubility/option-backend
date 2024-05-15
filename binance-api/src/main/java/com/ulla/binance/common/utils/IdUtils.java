package com.ulla.binance.common.utils;

/**
 * @author zhuyongdong
 * @Description ID生成器工具类
 * @since 2023/2/21 17:12
 */
public class IdUtils {

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成4位UUID
     * 
     * @return
     */
    public static String get4SimpleUUID() {
        return UUID.get4UUID();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成8位UUID
     * 
     * @return
     */
    public static String get8SimpleUUID() {
        return UUID.get8UUID();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成12位UUID
     * 
     * @return
     */
    public static String get12SimpleUUID() {
        return UUID.get12UUID();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成16位UUID
     * 
     * @return
     */
    public static String get16SimpleUUID() {
        return UUID.get16UUID();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成20位UUID
     * 
     * @return
     */
    public static String get20SimpleUUID() {
        return UUID.get20UUID();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成24位UUID
     * 
     * @return
     */
    public static String get24SimpleUUID() {
        return UUID.get24UUID();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成32位UUID
     * 
     * @return
     */
    public static String get32SimpleUUID() {
        return UUID.get32UUID();
    }

}
