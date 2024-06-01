package com.lcx.rabbitMQ.consumer;

import com.lcx.common.constant.RabbitMQ;
import com.lcx.common.constant.Supervise;
import com.lcx.common.util.RedisUtil;
import com.lcx.handler.SuperviseWebSocketHandler;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RabbitListener(queues = RabbitMQ.RATE_QUEUE)
public class RateInfoQueueReceiver {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SuperviseWebSocketHandler superviseWebSocketHandler;

    @RabbitHandler
    public void receive(String message) {
        String value= stringRedisTemplate.opsForValue().get(RedisUtil.getSuperviseKey(Supervise.RATE));
        // superviseRateWebSocket开启才发送信息，否则丢弃
        if(Objects.equals(value, Supervise.SUPERVISE_OPEN))
            superviseWebSocketHandler.sendStatusInfo(message);
    }
}
