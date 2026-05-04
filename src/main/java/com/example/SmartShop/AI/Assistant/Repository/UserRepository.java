package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * User repository for DB operations
 * Handles user-related persistence queries.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email (used for login & JWT validation)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists (used in signup validation)
     */
    boolean existsByEmail(String email);
}
