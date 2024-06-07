package com.lcx.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessage;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.security.auth.login.LoginException;
import java.util.Map;

public class UserWebsocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = request.getHeaders().getFirst("Authorization");
        if(token == null)
            throw new LoginException(ErrorMessage.NOT_LOGIN);

        // 解析token，获得id
        int uid = Integer.parseInt((String) StpUtil.getLoginIdByToken(token));
        attributes.put("uid", uid);

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
