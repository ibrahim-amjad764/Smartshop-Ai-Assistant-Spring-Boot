//package com.example.SmartShop.AI.Assistant.Dto;
//
///**
// * DTO for authentication response
// */
//public class AuthResponseDTO {
//
//    private final String token;  // ✅ token as String
//    private final String email;
//
//    /**
//     * Constructor-based initialization (immutable DTO)
//     */
//    public AuthResponseDTO(String token, String email) {
//        this.token = token;
//        this.email = email;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//}

package com.example.SmartShop.AI.Assistant.Dto;

import java.time.LocalDateTime;

/**
 * DTO for authentication response and user info.
 * Immutable object pattern ensures thread safety.
 */
public class AuthResponseDTO {

    private final String token;
    private final Long id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;

    /**
     * Constructor for AuthResponseDTO
     */
    public AuthResponseDTO(String token, Long id, String name, String email, LocalDateTime createdAt) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}