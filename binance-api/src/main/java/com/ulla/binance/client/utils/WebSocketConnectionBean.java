package com.ulla.binance.client.utils;

import com.ulla.binance.client.impl.WebsocketClientImpl;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description websocket管理工具
 * @since 2023/2/10 18:14
 */
@Data
public class WebSocketConnectionBean {

    private Integer clientId;

    private WebsocketClientImpl websocketClient;
}
