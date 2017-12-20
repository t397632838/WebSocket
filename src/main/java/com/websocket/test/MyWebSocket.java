package com.websocket.test;

import com.websocket.SocketMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by tangliang on 2017/12/8.
 */
@ServerEndpoint(value = "/websocketTest", encoders = ServerEncoder.class)
@Component
public class MyWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
//        try {
//            sendMessage(CommonConstant.CURRENT_WANGING_NUMBER.toString());
//        } catch (IOException e) {
//            System.out.println("IO异常");
//        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);

        //群发消息
        for (MyWebSocket item : webSocketSet) {
            try {
                SocketMessage sm = new SocketMessage();
                sm.setMessage(message);
                item.sendMessage(sm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    public void callback() throws Exception {
        // 发现消息
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //群发消息
        for (MyWebSocket item : webSocketSet) {
            try {
                SocketMessage sm = new SocketMessage();
                sm.setDate(df.format(new Date()));
                item.sendMessage(sm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(SocketMessage message) throws IOException {
        try {
            this.session.getBasicRemote().sendObject(message);
        } catch (EncodeException e) {
            e.printStackTrace();
        }
        //this.session.getAsyncRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {
//        for (MyWebSocket item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                continue;
//            }
//        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }
}
