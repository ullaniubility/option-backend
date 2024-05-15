package com.ulla.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 全局统一异常处理
 * @author zhuyongdong
 * @since 2023-01-03 15:35:22
 */
@Slf4j
public abstract class AbstractExceptionHandler {

    protected JSONObject buildErrorMap(Throwable ex) {
        JSONObject json = new JSONObject();
        if (ex instanceof RSAException || ex instanceof IllegalArgumentException) {
            json.put("code", HttpStatus.BAD_REQUEST.value());
            if (StringUtils.isNotBlank(ex.getMessage())) {
                json.put("msg", ex.getMessage());
            } else {
                json.put("msg", "无效的请求");
            }

        } else {
            json.put("code", HttpStatus.BAD_REQUEST.value());
            json.put("msg", "未知错误联系管理员");
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return json;
    }

}