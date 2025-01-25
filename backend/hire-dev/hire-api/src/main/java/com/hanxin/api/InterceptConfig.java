package com.hanxin.api;

import com.hanxin.api.intercept.EmailInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class InterceptConfig implements WebMvcConfigurer {

    @Bean
    public EmailInterceptor emailInterceptor() {
        return new EmailInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(emailInterceptor()).addPathPatterns("/passport/getEmailCode");
    }
}
