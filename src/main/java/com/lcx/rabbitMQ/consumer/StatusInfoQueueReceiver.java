package com.lcx.rabbitMQ.consumer;

import com.lcx.common.constant.RabbitMQ;
import com.lcx.common.constant.Supervise;
import com.lcx.common.util.RedisUtil;
import com.lcx.handler.SuperviseStatusWebSocketHandler;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RabbitListener(queues = RabbitMQ.STATUS_INFO_QUEUE)
public class StatusInfoQueueReceiver {

    @Resource
    private SuperviseStatusWebSocketHandler superviseStatusWebSocketHandler;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RabbitHandler
    public void receive(String StatusInfoJson) {
        String value= stringRedisTemplate.opsForValue().get(RedisUtil.getSuperviseKey(Supervise.STATUS));
        // superviseStatusWebSocket开启才发送信息，否则丢弃
        if(Objects.equals(value, Supervise.SUPERVISE_OPEN))
            superviseStatusWebSocketHandler.sendStatusInfo(StatusInfoJson);
    }

}