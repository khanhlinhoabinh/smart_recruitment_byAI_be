package com.fourctc.tuyendungthongminh_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // QUAN TRỌNG: Cho phép frontend Vite
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:5173"); // phòng trường hợp dùng 127.0.0.1

        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // GET, POST, PUT, PATCH, DELETE, OPTIONS
        config.setAllowCredentials(true); // Cho phép gửi token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);


        return new CorsFilter(source);
    }
}