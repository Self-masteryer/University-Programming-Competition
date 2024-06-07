package com.lcx.interceptor;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lcx.common.constant.ErrorMessage;
import com.lcx.common.constant.Role;
import com.lcx.common.constant.Supervise;
import com.lcx.common.exception.RoleVerificationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import javax.security.auth.login.LoginException;
import java.net.URI;
import java.util.Map;

public class AdminWebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response
            , WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从请求头中读取token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null)
            throw new LoginException(ErrorMessage.NOT_LOGIN);

        // 解析token，获得id
        int uid = Integer.parseInt((String) StpUtil.getLoginIdByToken(token));

        // 验证管理员身份
        SaSession saSession = StpUtil.getSessionByLoginId(uid);
        if (saSession.getInt(Role.ROLE) != Role.ADMIN)
            throw new RoleVerificationException(ErrorMessage.ROLE_VERIFICATION_EXCEPTION);

        // 获得query参数，判断监听事件
//        URI uri = request.getURI();
//        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();

        // 添加属性
        attributes.put("uid", uid);
//        attributes.put(Supervise.EVENT, queryParams.getFirst(Supervise.EVENT));

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
