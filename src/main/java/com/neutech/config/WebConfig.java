// src/main/java/com/neutech/config/WebConfig.java
package com.neutech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理员接口拦截
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**"); // 所有以 /admin 开头的接口都需要管理员权限
    }
}
