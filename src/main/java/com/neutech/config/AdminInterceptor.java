// src/main/java/com/neutech/config/AdminInterceptor.java
package com.neutech.config;

import com.neutech.entity.Users;
import com.neutech.util.NeutechUtil;
import com.neutech.vo.ResultJson;
import com.neutech.vo.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("=== AdminInterceptor 拦截请求 ===");
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("请求方法: " + request.getMethod());

        // 获取Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            System.out.println("提取的Token: " + token);

            if (StringUtils.isBlank(token)) {
                System.out.println("Token为空");
            } else {
                try {
                    Object decodedToken = NeutechUtil.decode(token);
                    System.out.println("解码结果类型: " + (decodedToken != null ? decodedToken.getClass().getName() : "null"));
                    System.out.println("解码结果: " + decodedToken);

                    if (decodedToken == null) {
                        System.out.println("Token解析失败");
                    } else if (decodedToken instanceof Users) {
                        Users user = (Users) decodedToken;
                        System.out.println("用户ID: " + user.getId());
                        System.out.println("用户名: " + user.getName());
                        System.out.println("IsAdmin: " + user.getIsAdmin());

                        // 检查用户是否为管理员
                        if (Boolean.TRUE.equals(user.getIsAdmin())) {
                            System.out.println("权限验证通过，放行请求");
                            return true; // 放行
                        } else {
                            System.out.println("用户不是管理员");
                        }
                    } else {
                        System.out.println("解码结果不是Users类型: " + decodedToken.getClass().getName());
                    }
                } catch (Exception e) {
                    System.out.println("Token解析异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("缺少Authorization header或格式不正确");
        }

        // 权限不足
        System.out.println("权限验证失败，返回403");
        response.setContentType("application/json;charset=utf-8");
        ResultJson result = ResultJson.getInstance(ResultCode.FORBID, null, "权限不足，需要管理员权限");
        response.getWriter().write(com.alibaba.fastjson.JSONObject.toJSONString(result));
        return false;
    }
}
