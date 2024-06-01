package com.lcx.config;

import com.lcx.handler.SuperviseWebSocketHandler;
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
    private SuperviseWebSocketHandler superviseWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册supervise端点
        registry.addHandler(superviseWebSocketHandler, "/supervise")
                .addInterceptors(new AdminWebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");

        // 注册user端点
        registry.addHandler(userWebSocketHandler, "/user")
                .addInterceptors(new UserWebsocketHandshakeInterceptor())
                .setAllowedOrigins("*");

    }

}
