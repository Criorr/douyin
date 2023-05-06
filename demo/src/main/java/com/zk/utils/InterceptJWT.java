package com.zk.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * InterceptJWT
 *
 * @author ZhengKai
 * @date 2023/4/24
 */

@Component
@Slf4j
public class InterceptJWT implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("token");
        log.info("path:"+String.valueOf(request.getRequestURL()));
        log.info("token:"+token);
        if (JWTUtils.checkToken(token)) {
            return true;
        }
        return false;
    }
    // TODO 使用JWT完成访问认证
}
