package com.lcx.handler;

import com.lcx.common.constant.Supervise;
import com.lcx.common.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

//  实时向管理员推送用户状态改变信息
@Slf4j
@Component
public class SuperviseRateWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 只有一个管理员
    private WebSocketSession session;
    private Map<String, Object> attributes;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        this.session=session;
        attributes=session.getAttributes();
        // 开启向消息队列发送打分消息
        stringRedisTemplate.opsForValue().set(RedisUtil.getSuperviseKey(Supervise.RATE),Supervise.SUPERVISE_OPEN);
        log.info("SuperviseRateWebSocket 连接建立: {}", attributes.get("uid"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        // 关闭向消息队列发送打分消息
        stringRedisTemplate.opsForValue().set(RedisUtil.getSuperviseKey(Supervise.RATE),Supervise.SUPERVISE_CLOSE);
        log.info("SuperviseRateWebSocket 连接关闭: {}", attributes.get("uid"));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
        log.error("SuperviseRateWebSocket 传输错误: {}", session.getId(), exception);
    }

    public void sendRateInfo(String rateInfo)  {
        try {
            session.sendMessage(new TextMessage(rateInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
