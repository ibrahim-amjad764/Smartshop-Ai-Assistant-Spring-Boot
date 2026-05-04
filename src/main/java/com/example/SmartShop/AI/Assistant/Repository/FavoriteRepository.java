package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.Favorite;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing user favorites.
 * Optimized to prevent N+1 queries and safely delete duplicates.
 */
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    boolean existsByUserAndProduct(User user, Product product);

    /**
     * Bulk delete favorites by user and productId (UUID).
     * ⚠ Use @Transactional to avoid issues with @Modifying bulk queries.
     */
    @Modifying
    @Transactional
    @Query("""
          DELETE FROM Favorite f
          WHERE f.user = :user
          AND f.product.id = :productId
      """)
    int deleteAllByUserAndProductId(User user, UUID productId);

    /**
     * Fetch all favorites with products and offers eagerly loaded.
     * Prevents N+1 query problem.
     */
    @EntityGraph(attributePaths = {
            "product",
            "product.offers",
            "product.offers.store"
    })
    List<Favorite> findAllByUser(User user);
}
