package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.*;
import com.example.SmartShop.AI.Assistant.Service.AuthService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authentication Controller
 * Handles login, signup, and profile update APIs.
 */
@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:3000") // Enable if using React frontend
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor-based dependency injection (recommended over @Autowired)
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login API
     * @param request login credentials
     * @return AuthResponseDTO containing JWT and user info
     */
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /**
     * Signup API
     * @param request registration info
     * @return AuthResponseDTO containing JWT and user info
     */
    @PostMapping("/signup")
    public AuthResponseDTO signup(@RequestBody SignupRequestDTO request) {
        return authService.signup(request);
    }

    /**
     * Update current user's profile
     * Reuses SignupRequestDTO (name, email)
     */
    @PatchMapping("/me")
    public AuthResponseDTO updateProfile(
            Authentication authentication,
            @RequestBody SignupRequestDTO request
    ) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = authentication.getName();
        return authService.updateProfile(email, request);
    }
}
