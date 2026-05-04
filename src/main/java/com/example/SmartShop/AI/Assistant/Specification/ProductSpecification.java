package com.example.SmartShop.AI.Assistant.Specification;

import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Entity.ProductCategory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductSpecification
 *
 * Provides dynamic, production-grade filtering for Product entities.
 * Features:
 * - Case-insensitive string matching
 * - Trim-safe inputs
 * - Supports multiple selections for brand, storage, and RAM
 * - Supports price, rating, and battery filters
 * - Distinct-safe to prevent duplicate results when joining tables
 */
public class ProductSpecification {

    /**
     * Builds a JPA Specification based on provided filter criteria.
     *
     * @param minPrice Minimum price filter (nullable)
     * @param maxPrice Maximum price filter (nullable)
     * @param brands   List of brands to filter (nullable/empty = no filter)
     * @param minRating Minimum rating filter (nullable)
     * @param storage  List of storage options (nullable/empty = no filter)
     * @param ram      List of RAM options (nullable/empty = no filter)
     * @param battery  Minimum battery capacity (nullable)
     * @return Specification<Product> for dynamic query
     */
    public static Specification<Product> filterProducts(
            Double minPrice,
            Double maxPrice,
            List<String> brands,
            Double minRating,
            List<String> storage,
            List<String> ram,
            Integer battery,
            ProductCategory category
    ) {

        return (root, query, cb) -> {

            // Ensure results are distinct
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // ================= PRICE =================
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // ================= BRAND =================
            if (brands != null && !brands.isEmpty()) {
                List<String> normalizedBrands = normalize(brands);
                predicates.add(cb.lower(root.get("brand")).in(normalizedBrands));
            }

            // ================= RATING =================
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            // ================= STORAGE =================
            if (storage != null && !storage.isEmpty()) {
                List<String> normalizedStorage = normalize(storage);
                predicates.add(cb.lower(root.get("storage")).in(normalizedStorage));
            }

            // ================= RAM =================
            if (ram != null && !ram.isEmpty()) {
                List<String> normalizedRam = normalize(ram);
                predicates.add(cb.lower(root.get("ram")).in(normalizedRam));
            }

            // ================= BATTERY =================
            if (battery != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("battery"), battery));
            }

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            // Combine all predicates with AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Utility method for case-insensitive and trim-safe string lists.
     *
     * @param values input string list
     * @return normalized list in lower-case and trimmed
     */
    private static List<String> normalize(List<String> values) {
        return values.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(v -> v.trim().toLowerCase())
                .collect(Collectors.toList());
    }
}