//package com.example.SmartShop.AI.Assistant;
//
//import com.example.SmartShop.AI.Assistant.Security.JwtAuthFilter;
//import com.example.SmartShop.AI.Assistant.Security.JwtService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class JwtAuthFilterTest {
//
//    @Mock
//    private JwtService jwtService;
//
//    @InjectMocks
//    private JwtAuthFilter jwtAuthFilter;
//
//    @AfterEach
//    void tearDown() {
//        SecurityContextHolder.clearContext();
//        System.out.println("SecurityContext cleared after test.");
//    }
//
//    @Test
//    void shouldSetAuthentication_WhenTokenIsValid() throws Exception {
//        System.out.println("Running: shouldSetAuthentication_WhenTokenIsValid");
//
//        String token = "valid-jwt-token";
//        String email = "test@example.com";
//
//        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
//        FilterChain filterChain = Mockito.mock(FilterChain.class);
//
//        Mockito.when(request.getHeader("Authorization"))
//                .thenReturn("Bearer " + token);
//
//        Mockito.when(jwtService.extractUsername(token)).thenReturn(email);
//        Mockito.when(jwtService.isTokenValid(token)).thenReturn(true);
//
//        jwtAuthFilter.doFilter(request, response, filterChain);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        assertNotNull(authentication);
//        assertEquals(email, authentication.getName());
//
//        Mockito.verify(filterChain).doFilter(request, response);
//
//        System.out.println("Authentication successfully set for: " + email);
//    }
//
//    @Test
//    void shouldNotSetAuthentication_WhenTokenIsInvalid() throws Exception {
//        System.out.println("Running: shouldNotSetAuthentication_WhenTokenIsInvalid");
//
//        String token = "invalid-jwt-token";
//
//        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
//        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
//        FilterChain filterChain = Mockito.mock(FilterChain.class);
//
//        Mockito.when(request.getHeader("Authorization"))
//                .thenReturn("Bearer " + token);
//
//        // Use Mockito.lenient() instead of plain lenient()
//        Mockito.lenient().when(jwtService.isTokenValid(token)).thenReturn(false);
//
//        // Prevent NullPointerException from getWriter()
//        StringWriter responseWriter = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(responseWriter);
//        Mockito.when(response.getWriter()).thenReturn(printWriter);
//
//        jwtAuthFilter.doFilter(request, response, filterChain);
//
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//
//        System.out.println("Invalid token handled correctly. No authentication set.");
//    }
//}