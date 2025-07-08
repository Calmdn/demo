package com.example.demo.config;

import com.example.demo.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",       // 排除登录接口
                        "/api/auth/register",    // 排除注册接口
                        "/api/auth/test-bcrypt", // 排除测试接口
                        "/api/auth/quick-test" ,  // 排除快速测试接口
                        "/api/test/**"  // 排除快速测试接口
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问
        String uploadPath = "file:" + System.getProperty("user.dir") + fileUploadConfig.getPath();

        registry.addResourceHandler(fileUploadConfig.getPath() + "**")
                .addResourceLocations(uploadPath);
    }
}