package com.takehometask.readingassignment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-local-origin:http://localhost:4200}")
    private String allowedOrigin;

    @Value("${app.cors.allowed-prod-origin:https://reading-assignment-ui.vercel.app}")
    private String allowedProdOrigin;



    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin, allowedProdOrigin)
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}