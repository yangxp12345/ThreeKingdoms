package org.yang.springboot.socket;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yang.business.active.IActive;
import org.yang.business.calc.DataCalc;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@ServerEndpoint("/webSocket/game")//前端页面请求的uri地址,其中{camp}是动态参数,通过@PathParam(value = "camp") 获取
@Component
@Slf4j
@Data
public class SocketServer {
    public static int sleep = 100;//停顿时间

    //保存所有客户端的session对象 用于交换数据 <用户名,传输数据对象>
    private final static Map<String, Session> sessionMap = new ConcurrentHashMap<>();


    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session) throws IOException {
        //请求参数
        Map<String, String> paramMap = session.getRequestParameterMap().entrySet().stream().map(unit -> new AbstractMap.SimpleEntry<>(unit.getKey(), unit.getValue().get(0))).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        String camp = paramMap.get("camp");
        String uuid = paramMap.get("uuid");
        log.info("客户端: {}, 阵容: {} 加入连接", uuid, camp);
        sessionMap.put(uuid, session);
    }

    //关闭连接时调用
    @OnClose
    public void onClose(Session session) {
        //请求参数
        Map<String, String> paramMap = session.getRequestParameterMap().entrySet().stream().map(unit -> new AbstractMap.SimpleEntry<>(unit.getKey(), unit.getValue().get(0))).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        String camp = paramMap.get("camp");
        String uuid = paramMap.get("uuid");
        sessionMap.remove(uuid);
        log.info("客户端: {}, 阵容: {} 断开连接", uuid, camp);

    }

    /**
     * 接收一个客户端信息
     *
     * @param message 接收的信息
     * @throws IOException 异常信息
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        Map<String, String> paramMap = session.getRequestParameterMap().entrySet().stream().map(unit -> new AbstractMap.SimpleEntry<>(unit.getKey(), unit.getValue().get(0))).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        log.info("接收到客户端{}的消息: {}", paramMap, message);
    }

    //异常调用
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("用户：{}，异常", session.getPathParameters().get("camp"), e);
    }

    /**
     * 给所有客户端发送消息
     *
     * @param camp   用户标识
     * @param active 角色的动作
     */
    public static void send(String camp, IActive active) {
        try {
            DataCalc.sleep(sleep);
            for (Session session : sessionMap.values()) {
                session.getBasicRemote().sendText(active.toString());
            }
        } catch (Exception e) {
            log.error("用户：{}，发送消息异常", camp, e);
        }
    }
}