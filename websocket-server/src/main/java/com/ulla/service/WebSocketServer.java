package com.ulla.service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ulla.common.enums.QueryKLineEnums;
import com.ulla.common.enums.StreamNameEnums;
import com.ulla.common.utils.StringUtils;
import com.ulla.modules.binance.qo.BinanceQuotationQo;
import com.ulla.modules.binance.qo.OrderRankQo;
import com.ulla.modules.binance.qo.SettleQo;
import com.ulla.modules.business.qo.WebsocketTransactionCategoryQo;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description WebScoket配置处理器
 * @author zhuyongdong
 * @since 2023-01-31 14:05:22
 */
@Slf4j
@Component
@ServerEndpoint("/webSocket/{userCode}")
public class WebSocketServer {

    private static ApplicationContext applicationContext;

    private WebSocketQuotationService webSocketQuotationService;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    private static final String STREAM_NAME = "streamName";

    /**
     * nginx location / { # root html; proxy_pass http://base-web; proxy_set_header Upgrade $http_upgrade;
     * proxy_set_header Connection "upgrade"; proxy_set_header Host $host; proxy_set_header X-Real-IP $remote_addr;
     * proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; # index index.html index.htm; }
     *
     *
     */

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
     */
    private static int onlineCount = 0;
    /**
     * 用来存放每个客户端对应的MyWebSocket对象
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */

    private Session session;
    /**
     * 接收 userCode
     */
    private String userCode = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userCode") String userCode) {
        this.session = session;
        this.userCode = userCode;
        if (webSocketMap.containsKey(userCode)) {
            webSocketMap.remove(userCode);
            webSocketMap.put(userCode, this);
        } else {
            webSocketMap.put(userCode, this);
            addOnlineCount();
        }
        log.info("用户连接:" + userCode + ",当前在线人数为:" + getOnlineCount());
        webSocketQuotationService = applicationContext.getBean(WebSocketQuotationService.class);
        webSocketQuotationService.updateOnlineCount(getOnlineCount());
        // sendHisMsg(userCode);
    }

    public void sendHisMsg(String userId) {

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userCode)) {
            webSocketQuotationService = applicationContext.getBean(WebSocketQuotationService.class);
            webSocketQuotationService.closeQuotation(userCode);
            webSocketMap.remove(userCode);
            onlineCount = webSocketMap.size();
        }
        log.info("用户退出:" + userCode + ",当前在线人数为:" + getOnlineCount());
        webSocketQuotationService.updateOnlineCount(getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        webSocketQuotationService = applicationContext.getBean(WebSocketQuotationService.class);
        // log.info("用户消息:" + userCode + ",报文:" + message);
        // 可以群发消息
        // 消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                // 解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                String streamName = StringUtils.camel2Underline(jsonObject.get(STREAM_NAME).toString(), false);
                switch (StreamNameEnums.valueOf(streamName).getColumnType()) {
                    case 0: {
                        BinanceQuotationQo binanceQuotationQo =
                            JSONObject.parseObject(jsonObject.toJSONString(), BinanceQuotationQo.class);
                        webSocketQuotationService.openQuotation(userCode, binanceQuotationQo);
                        break;
                    }
                    case 1: {
                        BinanceQuotationQo binanceQuotationQo =
                            JSONObject.parseObject(jsonObject.toJSONString(), BinanceQuotationQo.class);
                        webSocketQuotationService.queryHisQuotation(userCode, binanceQuotationQo);
                        break;
                    }
                    case 2: {
                        try {
                            WebsocketTransactionCategoryQo websocketTransactionCategoryQo =
                                JSONObject.parseObject(jsonObject.toJSONString(), WebsocketTransactionCategoryQo.class);
                            webSocketQuotationService.getSymbolList(userCode, websocketTransactionCategoryQo);
                        } catch (Exception e) {
                        }
                        break;
                    }
                    case 3: {
                        BinanceQuotationQo binanceQuotationQo =
                            JSONObject.parseObject(jsonObject.toJSONString(), BinanceQuotationQo.class);
                        if (!QueryKLineEnums.isExist(binanceQuotationQo.getKlineType())) {
                            String errorMsg = "{\"requestUuid\":\"" + binanceQuotationQo.getRequestUuid()
                                + "\",\"message\":\"暂未开放此行情!\"}";
                            sendMessage(errorMsg);
                            break;
                        }
                        webSocketQuotationService.openKlineQuotation(userCode, binanceQuotationQo);
                        break;
                    }
                    case 4: {
                        BinanceQuotationQo binanceQuotationQo =
                            JSONObject.parseObject(jsonObject.toJSONString(), BinanceQuotationQo.class);
                        if (!QueryKLineEnums.isExist(binanceQuotationQo.getKlineType())) {
                            String errorMsg = "{\"requestUuid\":\"" + binanceQuotationQo.getRequestUuid()
                                + "\",\"message\":\"暂未开放此行情!\"}";
                            sendMessage(errorMsg);
                            break;
                        }
                        if (binanceQuotationQo.getStartDataTime() == null
                            && binanceQuotationQo.getEndDataTime() == null) {
                            String errorMsg = "{\"requestUuid\":\"" + binanceQuotationQo.getRequestUuid()
                                + "\",\"message\":\"必须设定开始时间或结束时间查询条件!\"}";
                            sendMessage(errorMsg);
                            break;
                        }
                        webSocketQuotationService.queryHisKlineQuotation(userCode, binanceQuotationQo);
                        break;
                    }
                    case 5: {
                        SettleQo settleQo = JSONObject.parseObject(jsonObject.toJSONString(), SettleQo.class);
                        if (settleQo.getToken() == null) {
                            String errorMsg =
                                "{\"requestUuid\":\"" + settleQo.getRequestUuid() + "\",\"message\":\"无效的token!\"}";
                            sendMessage(errorMsg);
                            break;
                        }
                        webSocketQuotationService.orderCalculation(userCode, settleQo);
                        break;
                    }
                    case 6: {
                        SettleQo settleQo = JSONObject.parseObject(jsonObject.toJSONString(), SettleQo.class);
                        if (settleQo.getToken() == null) {
                            String errorMsg =
                                "{\"requestUuid\":\"" + settleQo.getRequestUuid() + "\",\"message\":\"无效的token!\"}";
                            sendMessage(errorMsg);
                            break;
                        }
                        webSocketQuotationService.orderResult(userCode, settleQo);
                        break;
                    }
                    case 7: {
                        OrderRankQo orderRankQo = JSONObject.parseObject(jsonObject.toJSONString(), OrderRankQo.class);
                        webSocketQuotationService.orderRank(userCode, orderRankQo);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("收到无效消息！消息内容：{}", message);
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userCode + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) {
        message = message.replaceAll("\\\\", "\"");
        synchronized (session) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                sendMessage(e.getMessage());
            }
        }
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        // log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
            webSocketMap.remove(userId);
        }
    }

    public static boolean queryUserIsOnline(String userId) {
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            return true;
        }
        return false;
    }

    public static void sendBatchMsg(String message) throws IOException {
        long size = webSocketMap.size();
        if (size <= 0) {
            log.error("当前没有在线客户端，无法推送消息");
        }
        Set<String> keys = webSocketMap.keySet();
        for (String key : keys) {
            webSocketMap.get(key).sendMessage(message);
        }

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
