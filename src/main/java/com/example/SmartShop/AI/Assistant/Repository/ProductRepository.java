package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; //  Enables dynamic filtering
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Product entities.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor for dynamic filtering support.
 */
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    /**
     * Fetches the top 10 products with titles containing the given string (case-insensitive).
     */
    List<Product> findTop10ByTitleContainingIgnoreCase(String title);

    /**
     *  NEW: Fetch top 20 products by title (used in AI smart search)
     * Optimized for limiting dataset before AI ranking
     */
    List<Product> findTop20ByTitleContainingIgnoreCase(String title);

    /**
     * Fetches products whose title contains the given string (case-insensitive).
     */
    List<Product> findByTitleContainingIgnoreCase(String title);

    /**
     * Fetches products by brand name, case-insensitive.
     */
    List<Product> findByBrandIgnoreCase(String brand);

    /**
     *  NEW: Fetch top 10 products by brand (used in AI smart search)
     */
    List<Product> findTop10ByBrandContainingIgnoreCase(String brand);

    /**
     *  NEW: Fetch latest 5 products (fallback when AI not available)
     * Requires 'createdAt' field in Product entity
     */
    List<Product> findTop5ByOrderByCreatedAtDesc();

    /**
     * Fetches products that are marked as featured (true).
     */
    List<Product> findByFeaturedTrue(Pageable pageable);

    /**
     * Fetch products by featured flag
     */
    List<Product> findByFeatured(Boolean featured);
}
