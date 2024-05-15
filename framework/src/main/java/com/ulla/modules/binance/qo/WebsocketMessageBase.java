package com.ulla.modules.binance.qo;

import lombok.Data;

/**
 * @author zhuyongdong
 * @Description websocket客户端消息模型
 * @since 2023/2/20 13:11
 */
@Data
public class WebsocketMessageBase {

    /**
     * 事件类型
     */
    String streamName;

}
