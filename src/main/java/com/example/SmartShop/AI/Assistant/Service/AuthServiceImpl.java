package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.AuthResponseDTO;
import com.example.SmartShop.AI.Assistant.Dto.LoginRequestDTO;
import com.example.SmartShop.AI.Assistant.Dto.SignupRequestDTO;
import com.example.SmartShop.AI.Assistant.Entity.User;
import com.example.SmartShop.AI.Assistant.Repository.UserRepository;
import com.example.SmartShop.AI.Assistant.Security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.time.LocalDateTime;

/**
 * Authentication Service Implementation
 * Handles login, signup, fetching current user, and updating profile
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ========================= LOGIN =========================
    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        System.out.println(" AuthService: Attempting login for: " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println(" AuthService: User not found = " + request.getEmail());
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println(" AuthService: Invalid password for " + request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        System.out.println(" AuthService: Login successful, token generated for " + user.getEmail());

        // Convert UUID → Long
        Long userId = user.getId().getMostSignificantBits();
        System.out.println(" Converted UUID to Long: " + userId);

        // Correct Instant → LocalDateTime conversion
        LocalDateTime createdAt = LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault());
        System.out.println(" User createdAt fetched: " + createdAt);

        return new AuthResponseDTO(
                token,
                userId,
                user.getName(),
                user.getEmail(),
                createdAt
        );
    }

    // ========================= SIGNUP =========================
    @Override
    public AuthResponseDTO signup(SignupRequestDTO request) {

        System.out.println(" AuthService: Signup request for email = " + request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            System.out.println(" AuthService: Email already registered = " + request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        System.out.println(" AuthService: User created successfully, ID = " + savedUser.getId());

        String token = jwtService.generateToken(savedUser.getEmail());

        // Convert UUID → Long
        Long userId = savedUser.getId().getMostSignificantBits();
        System.out.println(" Converted UUID to Long: " + userId);

        // Correct Instant → LocalDateTime conversion
        LocalDateTime createdAt = LocalDateTime.ofInstant(savedUser.getCreatedAt(), ZoneId.systemDefault());
        System.out.println(" User createdAt fetched: " + createdAt);

        return new AuthResponseDTO(
                token,
                userId,
                savedUser.getName(),
                savedUser.getEmail(),
                createdAt
        );
    }

    // ========================= GET CURRENT USER =========================
    @Override
    public AuthResponseDTO getCurrentUserByEmail(String email) {

        System.out.println("👤 AuthService: Fetching current user for " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println(" AuthService: User not found for email = " + email);
                    return new RuntimeException("User not found");
                });

        // Convert UUID → Long
        Long userId = user.getId().getMostSignificantBits();
        System.out.println(" Converted UUID to Long: " + userId);

        // Correct Instant → LocalDateTime conversion
        LocalDateTime createdAt = LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault());
        System.out.println(" User createdAt fetched: " + createdAt);

        return new AuthResponseDTO(
                null,
                userId,
                user.getName(),
                user.getEmail(),
                createdAt
        );
    }

    // ========================= UPDATE PROFILE =========================
    @Override
    public AuthResponseDTO updateProfile(String email, SignupRequestDTO request) {

        System.out.println(" AuthService: Update profile request for " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println(" AuthService: User not found for update = " + email);
                    return new RuntimeException("User not found");
                });

        // Update name
        if (request.getName() != null && !request.getName().isBlank()) {
            System.out.println(" Updating name: " + user.getName() + " → " + request.getName());
            user.setName(request.getName());
        }

        // Update email
        String token = null;

        if (request.getEmail() != null
                && !request.getEmail().isBlank()
                && !request.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(request.getEmail())) {
                System.out.println(" AuthService: Email already taken = " + request.getEmail());
                throw new RuntimeException("Email already in use");
            }

            System.out.println(" Updating email: " + user.getEmail() + " → " + request.getEmail());
            user.setEmail(request.getEmail());

            token = jwtService.generateToken(user.getEmail());
            System.out.println(" AuthService: New JWT generated for updated email");
        }

        userRepository.save(user);

        System.out.println(" Profile updated successfully for user ID = " + user.getId());

        // Convert UUID → Long
        Long userId = user.getId().getMostSignificantBits();
        System.out.println(" Converted UUID to Long: " + userId);

        // Correct Instant → LocalDateTime conversion
        LocalDateTime createdAt = LocalDateTime.ofInstant(user.getCreatedAt(), ZoneId.systemDefault());
        System.out.println(" User createdAt fetched: " + createdAt);

        return new AuthResponseDTO(
                token,
                userId,
                user.getName(),
                user.getEmail(),
                createdAt
        );
    }
}