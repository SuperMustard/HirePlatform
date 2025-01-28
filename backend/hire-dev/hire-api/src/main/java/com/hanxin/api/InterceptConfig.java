package com.hanxin.api;

import com.hanxin.api.intercept.EmailInterceptor;
import com.hanxin.api.intercept.JWTCurrentUserInterceptor;
import com.hanxin.api.intercept.SMSInterceptor;
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

    @Bean
    public SMSInterceptor smsInterceptor() { return new SMSInterceptor(); }

    @Bean
    public JWTCurrentUserInterceptor jwtCurrentUserInterceptor() {
        return new JWTCurrentUserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(emailInterceptor()).addPathPatterns("/passport/getEmailCode");
        registry.addInterceptor(smsInterceptor()).addPathPatterns("/passport/getSMSCode");

        registry.addInterceptor(jwtCurrentUserInterceptor()).addPathPatterns("/**");
    }
}
