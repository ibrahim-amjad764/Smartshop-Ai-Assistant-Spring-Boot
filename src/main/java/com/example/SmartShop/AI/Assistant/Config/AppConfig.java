package com.example.SmartShop.AI.Assistant.Config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Application-wide configuration class.
 * Provides beans for RestTemplate and PasswordEncoder.
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate bean with connection and read timeouts.
     * Useful for external API calls, including AI or Cloudinary requests.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        System.out.println("INFO: Initializing RestTemplate with timeout settings");

        RestTemplate restTemplate = builder

                .connectTimeout(Duration.ofSeconds(10))

                .readTimeout(Duration.ofSeconds(10))

                .build();

        System.out.println("INFO: RestTemplate initialized successfully");

        return restTemplate;
    }
}