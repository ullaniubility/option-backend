package com.ulla.rocketmq.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhuyongdong
 * @Description 消息序列化
 * @since 2023/3/7 14:55
 */
public class MessageConverter {

    public static byte[] convertObjectToByteArray(Object object) {
        return convertObjectToJSON(object).getBytes();
    }

    public static String convertObjectToJSON(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T convertJSONToObject(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

}
