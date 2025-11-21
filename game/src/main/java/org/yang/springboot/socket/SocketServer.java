package org.yang.springboot.socket;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yang.business.active.IActive;
import org.yang.business.calc.DataCalc;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/webSocket/game/{camp}")//前端页面请求的uri地址,其中{camp}是动态参数,通过@PathParam(value = "camp") 获取
@Component
@Slf4j
@Data
public class SocketServer {
    //保存所有客户端的session对象 用于交换数据 <用户名,传输数据对象>
    private final static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "camp") String camp) throws IOException {
        log.info("{} 加入连接", camp);
        sessionMap.put(camp, session);
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "camp") String camp) {
        sessionMap.remove(camp);
        log.info("{} 断开连接", camp);
    }

    /**
     * 接收一个客户端信息
     *
     * @param camp    接收客户端消息的用户id
     * @param message 接收的信息
     * @throws IOException 异常信息
     */
    @OnMessage
    public void onMessage(@PathParam(value = "camp") String camp, String message) throws IOException {
        log.info("接收到客户端{}的消息: {}", camp, message);
    }

    //异常调用
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("用户：{}，异常", session.getPathParameters().get("camp"), e);
    }

    /**
     * 给指定客户端发送消息
     *
     * @param camp   用户标识
     * @param active 角色的动作
     */
    public static void send(String camp, IActive active) {
        try {
            DataCalc.sleep(10);
            for (Session session : sessionMap.values()) {
                session.getBasicRemote().sendText(active.toString());
            }
        } catch (Exception e) {
            log.error("用户：{}，发送消息异常", camp, e);
        }
    }

}