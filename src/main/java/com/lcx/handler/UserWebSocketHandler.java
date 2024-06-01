package com.lcx.handler;


import com.alibaba.fastjson2.JSON;
import com.lcx.common.constant.RabbitMQ;
import com.lcx.common.constant.Supervise;
import com.lcx.common.util.RedisUtil;
import com.lcx.mapper.UserMapper;
import com.lcx.pojo.DAO.StatusInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class UserWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final ConcurrentMap<Integer, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 更新用户状态
        int uid=getUid(session);
        userMapper.updateStatus(uid, 1, LocalDateTime.now());

        // 判断是否向消息队列推送状态信息
        String value = stringRedisTemplate.opsForValue().get(RedisUtil.getSuperviseKey(Supervise.STATUS));
        if(Objects.equals(value, Supervise.SUPERVISE_OPEN)){

            // 构建json状态信息
            StatusInfo statusInfo = StatusInfo.builder().id(uid).status("在线").onlineTime(LocalDateTime.now()).build();
            String statusInfoJson = JSON.toJSONString(statusInfo);

            // 向消息队列发送用户在线消息
            rabbitTemplate.convertAndSend(RabbitMQ.TOPIC_EXCHANGE,RabbitMQ.STATUS_INFO_ROUTE,statusInfoJson);
        }

        // 保存webSocketSession
        sessionMap.put(uid, session);
        log.info("UserWebSocket 连接建立: {}", uid);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 更新用户状态
        int uid = getUid(session);
        userMapper.updateStatus(uid, 0, LocalDateTime.now());

        // 判断是否向消息队列推送状态信息
        String value = stringRedisTemplate.opsForValue().get(RedisUtil.getSuperviseKey(Supervise.STATUS));
        if(Objects.equals(value,Supervise.SUPERVISE_OPEN)){

            // 构建json状态信息
            StatusInfo statusInfo = StatusInfo.builder().id(uid).status("离线").onlineTime(LocalDateTime.now()).build();
            String statusInfoJson = JSON.toJSONString(statusInfo);

            // 向消息队列发送用户在线消息
            rabbitTemplate.convertAndSend(RabbitMQ.TOPIC_EXCHANGE,RabbitMQ.STATUS_INFO_ROUTE,statusInfoJson);
        }

        sessionMap.remove(uid);
        log.info("UserWebSocket 连接关闭: {}", uid);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("UserWebWebSocket 传输错误: {}", getUid(session), exception);
    }

    public int getUid(WebSocketSession session){
        Map<String, Object> attributes = session.getAttributes();
        return (int) attributes.get("uid");
    }
}
