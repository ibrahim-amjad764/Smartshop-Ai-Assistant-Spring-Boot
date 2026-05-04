package com.example.SmartShop.AI.Assistant.Config;

import com.example.SmartShop.AI.Assistant.Security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Spring Security Configuration
 *
 * Handles:
 * - JWT authentication
 * - CORS configuration
 * - Endpoint authorization
 * - Password encoding
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    //  Allow dynamic frontend URL (production ready)
    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Password Encoder Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main Security Filter Chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        System.out.println("INFO: Initializing Security Filter Chain...");

        http

                // Disable CSRF (JWT based system)
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Stateless session (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        //  Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/products/**", "/api/search/**").permitAll()
                        .requestMatchers("/api/stores/**").permitAll()

                        //  FIX: Allow AI endpoints (IMPORTANT)
                        .requestMatchers("/api/ai/**").permitAll()

                        // Preflight requests
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        //  Protected endpoints
                        .requestMatchers("/api/cart/**").authenticated()
                        .requestMatchers("/api/favorites/**").authenticated()



                        // Everything else secured
                        .anyRequest().authenticated()
                )

                // Add JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("INFO: Security configuration loaded successfully");

        return http.build();
    }

    /**
     * CORS Configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        System.out.println("INFO: Configuring CORS for frontend: " + frontendUrl);

        CorsConfiguration config = new CorsConfiguration();

        // Dynamic origin (supports production deployment)
        config.setAllowedOrigins(List.of(frontendUrl));

        // Allowed methods
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        // Allow all headers
        config.setAllowedHeaders(List.of("*"));

        //  FIX: Enable credentials (needed for JWT in headers sometimes)
        config.setAllowCredentials(true);

        //  OPTIONAL: expose Authorization header to frontend
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        System.out.println("INFO: CORS configuration applied");

        return source;
    }
}