package com.lcx.interceptor;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.constant.Role;
import com.lcx.common.exception.RoleVerificationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.security.auth.login.LoginException;
import java.util.Map;

public class SuperviseWebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response
            , WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从请求头中读取token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(token == null)
            throw new LoginException(ErrorMessageConstant.NOT_LOGIN);

        // 解析uid
        int uid = Integer.parseInt((String) StpUtil.getLoginIdByToken(token));
        // 验证管理员身份
        SaSession saSession = StpUtil.getSessionByLoginId(uid);
        if(saSession.getInt(Role.ROLE)!=Role.ADMIN)
            throw new RoleVerificationException(ErrorMessageConstant.ROLE_VERIFICATION_EXCEPTION);

        // 添加进属性
        attributes.put("uid", uid);

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
