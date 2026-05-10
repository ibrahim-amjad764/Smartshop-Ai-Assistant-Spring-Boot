// package com.example.SmartShop.AI.Assistant.Config;

// import com.example.SmartShop.AI.Assistant.Security.JwtAuthFilter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import org.springframework.beans.factory.annotation.Value;

// import java.util.List;

// /**
//  * Spring Security Configuration
//  *
//  * Handles:
//  * - JWT authentication
//  * - CORS configuration
//  * - Endpoint authorization
//  * - Password encoding
//  */
// @Configuration
// public class SecurityConfig {

//     private final JwtAuthFilter jwtAuthFilter;

//     //  Allow dynamic frontend URL (production ready)
//     @Value("${frontend.url:http://localhost:3000}")
//     private String frontendUrl;

//     public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
//         this.jwtAuthFilter = jwtAuthFilter;
//     }

//     /**
//      * Password Encoder Bean
//      */
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     /**
//      * Main Security Filter Chain
//      */
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//         System.out.println("INFO: Initializing Security Filter Chain...");

//         http

//                 // Disable CSRF (JWT based system)
//                 .csrf(csrf -> csrf.disable())

//                 // Enable CORS
//                 .cors(cors -> cors.configurationSource(corsConfigurationSource()))

//                 // Stateless session (JWT)
//                 .sessionManagement(session ->
//                         session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                 )

//                 // Authorization rules
//                 .authorizeHttpRequests(auth -> auth

//                         //  Public endpoints
//                         .requestMatchers("/api/auth/**").permitAll()
//                         .requestMatchers("/api/products/**", "/api/search/**").permitAll()
//                         .requestMatchers("/api/stores/**").permitAll()

//                         //  FIX: Allow AI endpoints (IMPORTANT)
//                         .requestMatchers("/api/ai/**").permitAll()

//                         // Preflight requests
//                         .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

//                         //  Protected endpoints
//                         .requestMatchers("/api/cart/**").authenticated()
//                         .requestMatchers("/api/favorites/**").authenticated()



//                         // Everything else secured
//                         .anyRequest().authenticated()
//                 )

//                 // Add JWT filter
//                 .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

//         System.out.println("INFO: Security configuration loaded successfully");

//         return http.build();
//     }

//     /**
//      * CORS Configuration
//      */
//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {

//         System.out.println("INFO: Configuring CORS for frontend: " + frontendUrl);

//         CorsConfiguration config = new CorsConfiguration();

//         // Dynamic origin (supports production deployment)
//         config.setAllowedOrigins(List.of(frontendUrl));

//         // Allowed methods
//         config.setAllowedMethods(List.of(
//                 "GET",
//                 "POST",
//                 "PUT",
//                 "PATCH",
//                 "DELETE",
//                 "OPTIONS"
//         ));

//         // Allow all headers
//         config.setAllowedHeaders(List.of("*"));

//         //  FIX: Enable credentials (needed for JWT in headers sometimes)
//         config.setAllowCredentials(true);

//         //  OPTIONAL: expose Authorization header to frontend
//         config.setExposedHeaders(List.of("Authorization"));

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", config);

//         System.out.println("INFO: CORS configuration applied");

//         return source;
//     }
// }
package com.example.SmartShop.AI.Assistant.Config;

import com.example.SmartShop.AI.Assistant.Security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // 🚀 Set your production frontend here via Railway ENV variable
    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        System.out.println("INFO: Initializing Security Filter Chain...");

        http
            // Disable CSRF for JWT auth
            .csrf(csrf -> csrf.disable())

            // Enable CORS with our config
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Stateless session (JWT)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                    // Public endpoints
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/products/**", "/api/search/**").permitAll()
                    .requestMatchers("/api/stores/**").permitAll()
                    .requestMatchers("/api/ai/**").permitAll()

                    // Preflight OPTIONS request allowed
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                    // Protected endpoints
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
     * CORS configuration — only this file now handles it
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        System.out.println("INFO: Configuring CORS for frontend: " + frontendUrl);

        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins: local dev + production
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                frontendUrl
        ));

        // Allowed HTTP methods
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

        // Allow credentials (cookies/JWT)
        config.setAllowCredentials(true);

        // Expose Authorization header if JWT in header is used
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        System.out.println("INFO: CORS configuration applied");

        return source;
    }
}