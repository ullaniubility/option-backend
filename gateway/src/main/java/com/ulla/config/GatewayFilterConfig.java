package com.ulla.config;

import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.ulla.cache.Cache;
import com.ulla.utils.XRsaUtil;

import cn.hutool.core.util.StrUtil;
import jodd.net.URLDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Description 防火墙协议
 * @author zhuyongdong
 * @since 2023-01-03 15:35:22
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j

public class GatewayFilterConfig implements GlobalFilter, Ordered {

    @Value("${RSAConstant.PRIVATE_KEY}")
    String PRIVATE_KEY;

    final Cache cache;

    final NoRequireTokenUriConfiguration noRequireTokenUriConfiguration;

    private static final String ERROR_MESSAGE = "Denial of service";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (RequestMethod.OPTIONS.name().equals(request.getMethodValue())) {
            return chain.filter(exchange);
        }
        URI requestUri = request.getURI();
        HttpHeaders headers = request.getHeaders();
        Builder mutate = request.mutate();
        // 1 获取时间戳
        Long dateTimestamp = getDateTimestamp(exchange.getRequest().getHeaders());
        // 2 获取RequestId
        String requestId = getRequestId(exchange.getRequest().getHeaders());
        // 3 获取签名
        String sign = getSign(exchange.getRequest().getHeaders());
        // 4：该接口是否需要token 才能访问
        if (isRequireToken(exchange)) {
            // 取出用户的token
            String token = getUserToken(exchange);
            // 判断用户的token 是否有效
            if (StringUtils.isEmpty(token)) {
                return FilterUtils.invalidToken(exchange);
            }
            Boolean hasKey = cache.hasKey(token);
            if (hasKey == null || !hasKey) {
                return FilterUtils.invalidToken(exchange);
            }
        }
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            try {
                // 5 修改请求参数,并获取请求参数
                Map<String, Object> paramMap = updateRequestParam(exchange);
                checkSign(sign, dateTimestamp, requestId, paramMap);
                return encryptParamsGet(paramMap, chain, exchange, request, response, requestUri, mutate, requestId);
            } catch (Exception e) {
                return FilterUtils.invalidUrl(exchange);
            }
        } else {
            // 6 post请求时，如果是文件上传之类的请求，不修改请求消息体
            if (isFile(exchange.getRequest().getHeaders())) {
                return chain.filter(exchange.mutate().request(request).build());
            }
            return encryptParamsPost(sign, dateTimestamp, requestId, chain, exchange, headers);
        }
    }

    private Mono<Void> encryptParamsGet(Map<String, Object> paramMap, GatewayFilterChain chain,
        ServerWebExchange exchange, ServerHttpRequest request, ServerHttpResponse response, URI requestUri,
        Builder mutate, String requestId) {
        if (!paramMap.isEmpty()) {
            URI uri;
            if (requestUri.toString().contains("param=")) {
                try {
                    uri = new URI(requestUri.toString().split("param")[0] + asUrlParams(paramMap));
                } catch (Exception e) {
                    e.printStackTrace();
                    return errorReturn(response, requestId, "512", "错误的URL请求");
                }
            } else {
                return errorReturn(response, requestId, "512", "错误的URL请求");
            }
            ServerHttpRequest build = mutate.uri(uri).build();
            return chain.filter(exchange.mutate().request(build).build());
        } else {
            return chain.filter(exchange.mutate().request(request).build());
        }
    }

    private Mono<Void> encryptParamsPost(String sign, Long dateTimestamp, String requestId, GatewayFilterChain chain,
        ServerWebExchange exchange, HttpHeaders httpHeaders) {
        // read & modify body
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            log.debug("body:{}", body);
            // 对原先的body进行修改操作
            String str = StrUtil.str(body, StandardCharsets.UTF_8);
            log.debug("str:{}", str);
            RSAPrivateKey rsaPrivateKey = XRsaUtil.getRSAPrivateKey(PRIVATE_KEY);
            String encrypt = URLDecoder.decode(XRsaUtil.privateDecrypt(str, rsaPrivateKey));
            log.debug("{}--------------------》收参:{}", encrypt, str);
            log.debug(encrypt);
            Gson gson = new Gson();
            Map<String, Object> paramMap = gson.fromJson(encrypt, Map.class);
            checkSign(sign, dateTimestamp, requestId, paramMap);
            return Mono.just(encrypt);
        });
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(httpHeaders);
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    long contentLength = headers.getContentLength();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    if (contentLength > 0L) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    return httpHeaders;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                    return outputMessage.getBody();
                }
            };
            return chain.filter(exchange.mutate().request(decorator).build());
        }));
    }

    public void checkSign(String sign, Long dateTimestamp, String requestId, Map<String, Object> paramMap) {
        String str = JSON.toJSONString(paramMap) + requestId + dateTimestamp;
        String tempSign = MD5Utils.md5Hex(str, Constants.ENCODE);
        if (!tempSign.equals(sign)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
    }

    public static String asUrlParams(Map<String, Object> source) {
        Map<String, Object> tmp = Maps.newHashMap();
        source.forEach((k, v) -> {
            if (k != null) {
                tmp.put(k, v);
            }
        });
        return Joiner.on("&").useForNull("").withKeyValueSeparator("=").join(tmp);
    }

    private Mono<Void> errorReturn(ServerHttpResponse response, String requestId, String code, String msg) {
        if ("302".equals(code)) {
            response
                .addCookie(ResponseCookie.from(Constants.TOKEN, "").path("/").maxAge(Duration.ofSeconds(0L)).build());
        }
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        Map<String, String> map = Maps.newHashMapWithExpectedSize(3);
        map.put("code", code);
        map.put("msg", msg);
        map.put("obj", null);
        log.info("{}--------------------》异常返回：{}", requestId, map);
        MDC.clear();
        return response.writeWith(Flux.just(response.bufferFactory().wrap(JSON.toJSONString(map).getBytes())));
    }

    private Long getDateTimestamp(HttpHeaders httpHeaders) {
        List<String> list = httpHeaders.get("timestamp");
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        long timestamp = Long.parseLong(list.get(0));
        long currentTimeMillis = System.currentTimeMillis();
        // 有效时长为5分钟
        if (currentTimeMillis - timestamp > 1000 * 60 * 5) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        return timestamp;
    }

    private String getRequestId(HttpHeaders headers) {
        List<String> list = headers.get("requestId");
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        String requestId = list.get(0);
        // 如果requestId存在redis中直接返回
        String temp = cache.getString(requestId);
        if (StringUtils.isNotBlank(temp)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        cache.put(requestId, requestId, 5L, TimeUnit.MINUTES);
        return requestId;
    }

    private String getSign(HttpHeaders headers) {
        List<String> list = headers.get("sign");
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        return list.get(0);
    }

    /**
     * 判断该接口是否需要token
     *
     * @param exchange
     * @return
     */
    private boolean isRequireToken(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if (noRequireTokenUriConfiguration.getUrls().contains(path)) {
            return false;// 不需要token
        }
        return Boolean.TRUE;
    }

    /**
     * 从请求头里获取用户的token
     *
     * @param exchange
     * @return
     */
    private String getUserToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(Constants.TOKEN);
        return token == null ? null : token.replace("bearer ", "");
    }

    /**
     * 判断是否为上传文件
     *
     * @param headers
     * @return
     */
    private boolean isFile(HttpHeaders headers) {
        List<String> list = headers.get("Content-Type");
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.get(0).indexOf("multipart/form-data") >= 0;
    }

    /**
     * 修改前端传的参数
     */
    private Map<String, Object> updateRequestParam(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (StringUtils.isNotBlank(query) && query.contains("param")) {
            try {
                String[] split = query.split("=");
                RSAPrivateKey rsaPrivateKey = XRsaUtil.getRSAPrivateKey(PRIVATE_KEY);
                String param = URLDecoder.decode(XRsaUtil.privateDecrypt(split[1], rsaPrivateKey));
                Field targetQuery = uri.getClass().getDeclaredField("query");
                targetQuery.setAccessible(true);
                targetQuery.set(uri, param);
                return getParamMap(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new TreeMap<>();
    }

    private Map<String, Object> getParamMap(String param) {
        Map<String, Object> map = new TreeMap<>();
        String[] split = param.split("&");
        for (String str : split) {
            String[] params = str.split("=");
            if (params.length > 1) {
                map.put(params[0], params[1]);
            } else {
                map.put(params[0], null);
            }
        }
        return map;
    }

    /** 顺序:数字越小，越先执行 */
    @Override
    public int getOrder() {
        return -2;
    }
}
