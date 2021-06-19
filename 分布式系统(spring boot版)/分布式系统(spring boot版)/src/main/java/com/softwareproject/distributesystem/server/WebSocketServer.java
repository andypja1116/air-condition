package com.softwareproject.distributesystem.server;

import org.springframework.stereotype.Component;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value="/main/{userId}")
public class WebSocketServer {

    // ConcurrentHashMap 就是一一种简易的缓存中间件
    public static Map<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<String, WebSocketServer>();
    private String userName;
    private Session session;

    @OnOpen
    public void on_open(Session session, @PathParam("userId")String userName) throws IOException {
        this.session = session;
        this.userName = userName;
        if (WebSocketServer.webSocketMap.containsKey(userName)) {
            // 判断是否已经在连接池中
            WebSocketServer.webSocketMap.remove(userName);
        }
        WebSocketServer.webSocketMap.put(userName, this);
        //System.out.println("有新的连接:" + id);
    }

    // 发送消息
    public void send_message(String message) throws IOException {
        if (null == this.session) {   // 相关session 已经关闭
            System.out.println("相关连接已经关闭");
        }
        else {
            this.session.getAsyncRemote().sendText(message);
        }
    }

}
