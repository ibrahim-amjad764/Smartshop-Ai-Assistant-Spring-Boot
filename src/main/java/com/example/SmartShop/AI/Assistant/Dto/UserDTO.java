package com.example.SmartShop.AI.Assistant.Dto;

/**
 * Lightweight DTO for returning user info.
 */
public class UserDTO {

    private Long id;
    private String email;

    public UserDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
}