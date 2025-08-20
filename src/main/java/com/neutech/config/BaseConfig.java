package com.neutech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.baomidou.mybatisplus.annotation.DbType;

@Configuration
public class BaseConfig implements WebMvcConfigurer {

    // 跨域配置
    @Bean
    public CorsFilter getCorsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://127.0.0.1"); // 允许的源
        configuration.addAllowedMethod("*"); // 允许的方法
        configuration.addAllowedHeader("*"); // 允许的请求头

        configuration.addAllowedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 配置路径

        return new CorsFilter(source);
    }

    // MyBatis Plus 配置
    @Bean
    public MybatisPlusInterceptor getMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 分页插件
        return interceptor;
    }

    // 密码加密配置
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(); // 使用 BCrypt 加密算法
    }

    // 静态资源路径配置（头像上传路径）
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取上传文件的存储路径，确保路径一致
        String uploadDir = "D:\\uploads\\avatars";

        // 映射文件访问路径，允许访问上传的头像文件
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations(uploadDir); // 配置上传文件路径
    }
}
