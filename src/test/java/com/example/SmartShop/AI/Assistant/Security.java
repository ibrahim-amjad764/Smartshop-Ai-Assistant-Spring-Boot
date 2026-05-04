//package com.example.SmartShop.AI.Assistant;
//
//import com.example.SmartShop.AI.Assistant.Security.JwtService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JwtServiceTest {
//
//    private JwtService jwtService;
//
//    @BeforeEach
//    void setup() {
//        // 32-character secret for HS256 (UTF-8 bytes)
//        jwtService = new JwtService("testsecret1234567890123456789012");
//    }
//
//    @Test
//    void testGenerateToken() {
//        String email = "test@example.com";
//        String token = jwtService.generateToken(email);
//        assertNotNull(token);
//        assertTrue(token.startsWith("eyJhbGciOiJIUzI1NiJ9")); // JWT header check
//    }
//
//    @Test
//    void testExtractUsername() {
//        String email = "test@example.com";
//        String token = jwtService.generateToken(email);
//        String extractedEmail = jwtService.extractUsername(token);
//        assertEquals(email, extractedEmail);
//    }
//
//    @Test
//    void testIsTokenValid() {
//        String email = "test@example.com";
//        String token = jwtService.generateToken(email);
//        assertTrue(jwtService.isTokenValid(token));
//    }
//}