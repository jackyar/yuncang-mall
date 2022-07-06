package com.yanyh.mall.config;

import com.yanyh.mall.handler.AccessLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: yanyh
 * @Date: 2022/7/5
 * @Description:
 *
 * web mvc相关配置，添加拦截器
 */
@Configuration
public class LocalWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessLimitInterceptor()).addPathPatterns("/sale/**");
    }
}
