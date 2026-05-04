package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.CartItem;
import com.example.SmartShop.AI.Assistant.Entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for CartItem entity.
 * Optimized to fetch product data with one query (prevents N+1 issue)
 */
public interface CartRepository extends JpaRepository<CartItem, UUID> {

  /**
   * Retrieves all cart items for a specific user and eagerly loads the associated
   * user and product entities.
   * This prevents N+1 query issues by loading related entities in one query.
   * 
   * @param user the user whose cart items are to be fetched
   * @return List of cart items with associated product and user
   */
  @EntityGraph(attributePaths = { "user", "product" })
  List<CartItem> findAllByUser(User user);

  /**
   * Retrieves a cart item based on the user and product ID.
   * This is helpful when checking if a specific product is in the user's cart.
   * 
   * @param user      the user associated with the cart item
   * @param productId the ID of the product
   * @return Optional containing the cart item if found, or empty if not found
   */
  Optional<CartItem> findByUserAndProductId(User user, java.util.UUID productId);

  /**
   * Checks if a cart item with a specific user and product ID exists.
   * This is useful for checking the presence of a product in the user's cart.
   * 
   * @param user      the user associated with the cart item
   * @param productId the ID of the product
   * @return true if the cart item exists, false otherwise
   */
  boolean existsByUserAndProductId(User user, java.util.UUID productId);

  /**
   * Deletes a cart item based on the user and product ID.
   * This removes the specified product from the user's cart.
   * 
   * @param user      the user associated with the cart item
   * @param productId the ID of the product to be removed
   */
  void deleteByUserAndProductId(User user, java.util.UUID productId);

  /**
   * Deletes all cart items associated with the user.
   * This is useful for clearing the user's entire cart.
   * <p>
   * Added @Modifying and @Transactional annotations to ensure Spring Data JPA
   * properly handles this bulk delete operation. Without these, Spring may throw
   * exceptions for non-transactional bulk modifications.
   * </p>
   * 
   * @param user the user whose cart items are to be deleted
   */
  @Modifying
  @Transactional
  void deleteAllByUser(User user);
}