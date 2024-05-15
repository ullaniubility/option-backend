package com.ulla.config;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSONObject;

import reactor.core.publisher.Mono;

/**
 * @Description 过滤工具
 * @author zhuyongdong
 * @since 2023-01-03 15:35:22
 */
public final class FilterUtils {

    private FilterUtils() {

    }

    public static Mono<Void> invalidToken(ServerWebExchange exchange) {
        JSONObject json = new JSONObject();
        json.put("code", HttpStatus.UNAUTHORIZED.value());
        json.put("msg", "无效的token");
        return buildReturnMono(json, exchange);
    }

    public static Mono<Void> invalidUrl(ServerWebExchange exchange) {
        JSONObject json = new JSONObject();
        json.put("code", HttpStatus.BAD_REQUEST.value());
        json.put("msg", "无效的请求");
        return buildReturnMono(json, exchange);
    }

    public static Mono<Void> buildReturnMono(JSONObject json, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        byte[] bits = json.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

}
