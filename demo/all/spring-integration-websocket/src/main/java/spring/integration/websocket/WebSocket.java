package spring.integration.websocket;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocket {

    private Session session;

    private String name;

    private static ConcurrentHashMap<String, WebSocket> webSocketSet = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "name") String name) {
        this.session = session;
        this.name = name;
        webSocketSet.put(name, this);
        System.out.println(String.format("[WebSocket] %s 连接成功，当前连接人数为：= %d", this.name, webSocketSet.size()));
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this.name);
        System.out.println(String.format("[WebSocket] %s 退出成功，当前连接人数为：= %d", this.name, webSocketSet.size()));
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(String.format("[WebSocket] %s 收到消息：%s", this.name, message));
        groupSending(message);
    }

    public void groupSending(String meassage) {
        for (String name : webSocketSet.keySet()) {
            if (!ObjectUtil.equal(name, this.name)) {
                try {
                    webSocketSet.get(name).session.getBasicRemote().sendText(meassage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void appointSending(String name, String message) {
        try {
            webSocketSet.get(name).session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
