package com.lcx.rabbitMQ.consumer;

import com.lcx.common.constant.RabbitMQ;
import com.lcx.handler.SuperviseWebSocketHandler;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitMQ.STATUS_INFO_QUEUE)
public class StatusInfoQueueReceiver {

    @Resource
    private SuperviseWebSocketHandler superviseWebSocketHandler;

    @RabbitHandler
    public void receive(String StatusInfoJson) {
        superviseWebSocketHandler.sendStatusInfo(StatusInfoJson);
    }

}