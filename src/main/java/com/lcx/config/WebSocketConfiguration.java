package com.lcx.config;

import com.lcx.handler.SuperviseWebSocketHandler;
import com.lcx.interceptor.SuperviseWebSocketHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册Supervise端点
        registry.addHandler(new SuperviseWebSocketHandler(), "/supervise")
                .addInterceptors(new SuperviseWebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");
    }

}
