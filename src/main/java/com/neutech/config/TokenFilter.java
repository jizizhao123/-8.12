// src/main/java/com/neutech/config/TokenFilter.java
package com.neutech.config;

import com.alibaba.fastjson.JSONObject;
import com.neutech.util.NeutechUtil;
import com.neutech.vo.ResultCode;
import com.neutech.vo.ResultJson;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class TokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        System.out.println("进入过滤器");
        System.out.println(request.getMethod() + "==========" + request.getRequestURI());

        // 包装请求以支持重复读取
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }

        // 如果是OPTIONS请求，直接放行并设置CORS头部
        if ("OPTIONS".equals(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 白名单：不需要token验证的接口
        if ("/users/login".equals(request.getRequestURI()) ||
                "/users/register".equals(request.getRequestURI()) ||
                "/userApplication/submit".equals(request.getRequestURI()) ||
                "/announcement/list".equals(request.getRequestURI()) ||           // 公告列表
                "/announcement/list/page".equals(request.getRequestURI()) ||     // 分页公告列表
                request.getRequestURI().startsWith("/announcement/detail/")) {   // 公告详情
            filterChain.doFilter(request, servletResponse);
            return;
        }

        // 获取Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (StringUtils.isBlank(token)) {
                handleUnauthorizedResponse(response);
                return;
            }

            try {
                Object decodedToken = NeutechUtil.decode(token);
                if (decodedToken == null) {
                    System.out.println("Token解析失败");
                    handleUnauthorizedResponse(response);
                    return;
                }
                System.out.println("Token验证成功");
            } catch (Exception e) {
                System.out.println("Token解析异常: " + e.getMessage());
                handleUnauthorizedResponse(response);
                return;
            }
        } else {
            System.out.println("缺少Authorization header或格式不正确");
            handleUnauthorizedResponse(response);
            return;
        }

        // 通过过滤器
        filterChain.doFilter(request, servletResponse);
    }

    private void handleUnauthorizedResponse(HttpServletResponse response) throws IOException {
        ResultJson resultJson = ResultJson.getInstance(ResultCode.UNAUTHORIZED, null, "非法请求");
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1");
        response.setContentType("application/json;charset=utf-8;");
        response.getWriter().write(JSONObject.toJSONString(resultJson));
    }
}