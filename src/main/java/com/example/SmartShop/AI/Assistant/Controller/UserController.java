//package com.example.SmartShop.AI.Assistant.Controller;
//
//import com.example.SmartShop.AI.Assistant.Dto.UserDTO;
//import com.example.SmartShop.AI.Assistant.Entity.User;
//import com.example.SmartShop.AI.Assistant.Repository.UserRepository;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * User Controller
// * Provides endpoints to interact with user data
// */
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:3000") // Frontend React
//public class UserController {
//
//    private final UserRepository userRepository;
//
//    public UserController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    /**
//     * Get all users (with pagination support)
//     * @param page Page number (optional)
//     * @param size Page size (optional)
//     * @return List of user DTOs
//     */
//    @GetMapping
//    public ResponseEntity<List<UserDTO>> getAllUsers(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        List<User> users = userRepository.findAll(pageable).getContent();
//
//        // Convert entities to DTOs to avoid exposing sensitive data
//        List<UserDTO> userDTOs = users.stream()
//                .map(user -> new UserDTO(user.getId(), user.getEmail()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(userDTOs);
//    }
//}

package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.AuthResponseDTO;
import com.example.SmartShop.AI.Assistant.Dto.SignupRequestDTO;
import com.example.SmartShop.AI.Assistant.Service.AuthService;
import com.example.SmartShop.AI.Assistant.Security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AuthService authService;
    private final JwtService jwtService;

    public UserController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody SignupRequestDTO request) {

        try {
            String token = tokenHeader.replace("Bearer ", "").trim();
            String email = jwtService.extractUsername(token);

            logger.info("PATCH /auth/me called for user={}", email);

            AuthResponseDTO response = authService.updateProfile(email, request);

            logger.info("Profile updated successfully for user={}", response.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in PATCH /auth/me", e);
            return ResponseEntity.status(500)
                    .body("Server error: " + e.getMessage());
        }
    }
}