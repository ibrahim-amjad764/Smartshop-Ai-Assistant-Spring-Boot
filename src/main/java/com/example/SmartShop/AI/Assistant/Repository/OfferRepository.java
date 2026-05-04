package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

  List<Offer> findByProduct_TitleContainingIgnoreCaseOrStore_NameContainingIgnoreCase(String productTitle,
      String storeName);

    List<Offer> findByProduct_IdAndAvailableTrue(UUID productId);

  /* ------------------- BY PRODUCT ID ------------------- */

  /**
   * Fetch all available offers by product ID, paginated.
   */
  Page<Offer> findByProduct_IdAndAvailableTrue(UUID productId, Pageable pageable);

  /**
   * Fetch the cheapest available offer by product ID.
   */
  Optional<Offer> findFirstByProduct_IdAndAvailableTrueOrderByPriceAsc(UUID productId);

  Page<Offer> findByStore_IdAndAvailableTrue(UUID storeId, Pageable pageable);

  /* ------------------- SEARCH ------------------- */

  /**
   * Flexible search across product title, brand, and store name with optional
   * budget, paginated.
   */
  @Query("""
          SELECT o
          FROM Offer o
          WHERE (
              LOWER(o.product.title) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(o.product.brand) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(o.store.name) LIKE LOWER(CONCAT('%', :query, '%'))
          )
          AND (:budget IS NULL OR o.price <= :budget)
      """)
  Page<Offer> search(@Param("query") String query, @Param("budget") Double budget, Pageable pageable);

  /**
   * Search by product title or store name (case-insensitive).
   */
  @Query("""
          SELECT o
          FROM Offer o
          JOIN o.product p
          JOIN o.store s
          WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
             OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%'))
      """)
  Page<Offer> searchByProductOrStore(@Param("query") String query, Pageable pageable);

}
