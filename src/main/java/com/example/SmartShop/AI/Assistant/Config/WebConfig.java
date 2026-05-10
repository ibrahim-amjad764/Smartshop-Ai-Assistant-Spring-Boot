package com.example.SmartShop.AI.Assistant.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     *  Global CORS configuration
     * Purpose: Allow frontend (React/Vite) to access backend APIs during development
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allow all API endpoints
                .allowedOrigins("http://localhost:3000", "http://localhost:5173", "https://friendly-duckanoo-470a23.netlify.app")  // Both React dev servers
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")  // Allow all HTTP methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true);  // Allow cookies/credentials
    }
}
