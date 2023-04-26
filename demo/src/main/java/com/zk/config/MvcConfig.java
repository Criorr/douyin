package com.zk.config;

import com.zk.utils.InterceptJWT;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MvcConfig
 *
 * @author ZhengKai
 * @date 2023/4/24
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptJWT())
                .excludePathPatterns(
                        "/douyin/user/login",
                        "/douyin/user/register",
                        "/douyin/feed"
        );
    }
}
