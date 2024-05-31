package com.lcx.config;

import com.lcx.common.constant.RabbitMQ;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitMQConfig {

    @Bean
    public Queue statusQueue(){
        return new Queue(RabbitMQ.STATUS_INFO_QUEUE);
    }

    @Bean
    public Queue rateQueue(){
        return new Queue(RabbitMQ.RATE_QUEUE);
    }

    @Bean
    public Queue scoreQueue(){
        return new Queue(RabbitMQ.SCORE_QUEUE);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(RabbitMQ.TOPIC_EXCHANGE);
    }

    @Bean
    public Binding statusBinding(){
        return BindingBuilder.bind(statusQueue()).to(topicExchange()).with(RabbitMQ.STATUS_INFO_ROUTE);
    }

    @Bean
    public Binding rateBinding(){
        return BindingBuilder.bind(rateQueue()).to(topicExchange()).with(RabbitMQ.RATE_ROUTE);
    }

    @Bean
    public Binding scoreBinding(){
        return BindingBuilder.bind(scoreQueue()).to(topicExchange()).with(RabbitMQ.SCORE_ROUTE);
    }

}
