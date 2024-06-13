package com.lcx.config;

import com.lcx.handler.SuperviseRateWebSocketHandler;
import com.lcx.handler.SuperviseStatusWebSocketHandler;
import com.lcx.handler.UserWebSocketHandler;
import com.lcx.interceptor.AdminWebSocketHandshakeInterceptor;
import com.lcx.interceptor.UserWebsocketHandshakeInterceptor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Slf4j

public class WebSocketConfiguration implements WebSocketConfigurer {

    @Resource
    private UserWebSocketHandler userWebSocketHandler;
    @Resource
    private SuperviseStatusWebSocketHandler superviseStatusWebSocketHandler;
    @Resource
    private SuperviseRateWebSocketHandler superviseRateWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册superviseStatus端点
        registry.addHandler(superviseStatusWebSocketHandler, "/superviseStatus")
                .addInterceptors(new AdminWebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

        // 注册superviseRate端点
        registry.addHandler(superviseRateWebSocketHandler, "/superviseRate")
                .addInterceptors(new AdminWebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

        // 注册user端点
        registry.addHandler(userWebSocketHandler, "/user")
                .addInterceptors(new UserWebsocketHandshakeInterceptor())
                .setAllowedOrigins("*");

    }

}
