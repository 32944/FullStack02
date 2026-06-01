package com.itheima.graduation.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.graduation.utils.JwtUtils;
import com.itheima.graduation.utils.Result;
import com.itheima.graduation.utils.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");//前端放jwt的地方
        if (token == null || !token.startsWith("Bearer ")) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED);
            return false;
        }
        token = token.substring(7);
        try {
            if (jwtUtils.isTokenExpired(token)) {
                writeErrorResponse(response, ResultCode.TOKEN_EXPIRED);
                return false;
            }
            jwtUtils.parseToken(token);
            return true;
        } catch (Exception e) {
            writeErrorResponse(response, ResultCode.TOKEN_INVALID);
            return false;
        }
    }
//作用是拦截未登录裸调用后端的接口
    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Result<Void> result = Result.error(resultCode);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
