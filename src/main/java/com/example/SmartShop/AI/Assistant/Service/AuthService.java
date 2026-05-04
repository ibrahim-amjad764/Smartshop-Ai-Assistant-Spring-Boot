package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.AuthResponseDTO;
import com.example.SmartShop.AI.Assistant.Dto.LoginRequestDTO;
import com.example.SmartShop.AI.Assistant.Dto.SignupRequestDTO;

/**
 * Authentication Service Interface
 *
 * Purpose:
 * Defines the contract for user authentication and profile management.
 * Supports login, signup, fetching the current user, and profile updates.
 */
public interface AuthService {

    /**
     * Authenticates a user with email and password.
     *
     * @param request the login request DTO containing email and password
     * @return AuthResponseDTO containing JWT token and user info
     */
    AuthResponseDTO login(LoginRequestDTO request);

    /**
     * Registers a new user in the system.
     *
     * @param request the signup request DTO containing name, email, and password
     * @return AuthResponseDTO containing JWT token and user info
     */
    AuthResponseDTO signup(SignupRequestDTO request);

    /**
     * Fetches the current authenticated user by their email.
     *
     * @param email the email of the user
     * @return AuthResponseDTO containing user info
     */
    AuthResponseDTO getCurrentUserByEmail(String email);

    /**
     * Updates the current user's profile (e.g., name or email)
     * and returns the updated authentication response.
     *
     * @param email   user's email (from authentication)
     * @param request DTO containing updated name/email
     * @return updated AuthResponseDTO
     */
    AuthResponseDTO updateProfile(String email, SignupRequestDTO request);
}