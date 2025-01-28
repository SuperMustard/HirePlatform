package com.hanxin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
@Slf4j
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // allow cors, all domain name
        config.addAllowedOriginPattern("*");
        // set cookie
        config.setAllowCredentials(true);
        // set allowed request method
        config.addAllowedMethod("*");
        // set allowed header
        config.addAllowedHeader("*");
        // add url map path
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        log.error("setting cors web fileter");
        return new CorsWebFilter(source);
    }
}
