package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.FavoriteProductDTO;
import com.example.SmartShop.AI.Assistant.Service.FavoriteService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID; // ✅ IMPORTANT: Product uses UUID

/**
 * REST controller for managing user favorites.
 * Handles API requests and delegates business logic to FavoriteService.
 *
 * Uses UUID for productId to match Product entity primary key.
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private static final Logger log = LoggerFactory.getLogger(FavoritesController.class);

    private final FavoriteService favoriteService;

    public FavoritesController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // =========================================================
    // GET ALL FAVORITES
    // =========================================================
    /**
     * Returns all favorite products for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<FavoriteProductDTO>> getFavorites(Authentication auth) {

        String email = extractEmail(auth);

        log.info("[FavoritesController] Fetching favorites for user={}", email);

        List<FavoriteProductDTO> favorites = favoriteService.getFavoritesByEmail(email);

        return ResponseEntity.ok(favorites);
    }

    // =========================================================
    // ADD FAVORITE
    // =========================================================
    /**
     * Adds a product to user's favorites.
     *
     * Example API:
     * POST /api/favorites/{productId}
     *
     * productId is UUID.
     */
    @PostMapping("/{productId}")
    public ResponseEntity<Void> addFavorite(Authentication auth,
                                            @PathVariable UUID productId) {

        String email = extractEmail(auth);

        log.info("[FavoritesController] Adding favorite | user={} productId={}", email, productId);

        try {

            favoriteService.addFavoriteByEmail(email, productId);

            log.info("[FavoritesController] Favorite added successfully");

            return ResponseEntity.status(201).build();

        } catch (IllegalStateException e) {

            log.warn("[FavoritesController] Duplicate favorite detected | user={} productId={}", email, productId);

            return ResponseEntity.status(409).build();

        } catch (EntityNotFoundException e) {

            log.warn("[FavoritesController] Product or User not found: {}", e.getMessage());

            return ResponseEntity.notFound().build();
        }
    }

    // =========================================================
    // REMOVE FAVORITE
    // =========================================================
    /**
     * Removes a product from user's favorites.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(Authentication auth,
                                               @PathVariable UUID productId) {

        String email = extractEmail(auth);

        log.info("[FavoritesController] Removing favorite | user={} productId={}", email, productId);

        boolean removed = favoriteService.removeFavoriteByEmail(email, productId);

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // CHECK FAVORITE
    // =========================================================
    /**
     * Checks if a product is already in user's favorites.
     */
    @GetMapping("/{productId}/check")
    public ResponseEntity<Boolean> checkFavorite(Authentication auth,
                                                 @PathVariable UUID productId) {

        String email = extractEmail(auth);

        boolean isFavorite = favoriteService.isFavoriteByEmail(email, productId);

        log.info("[FavoritesController] Check favorite | user={} productId={} result={}",
                email, productId, isFavorite);

        return ResponseEntity.ok(isFavorite);
    }

    // =========================================================
    // HELPER METHOD
    // =========================================================
    /**
     * Extracts authenticated user's email from Spring Security Authentication.
     */
    private String extractEmail(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {

            log.warn("Unauthorized access attempt");

            throw new EntityNotFoundException("User not authenticated");
        }

        return auth.getName(); // email is used as username
    }
}