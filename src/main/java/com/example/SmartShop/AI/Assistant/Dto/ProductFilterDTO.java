package com.example.SmartShop.AI.Assistant.Dto;

import com.example.SmartShop.AI.Assistant.Entity.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * ProductFilterDTO
 *
 * Used for dynamic product filtering.
 * Includes validation to prevent invalid query values.
 */
public record ProductFilterDTO(

        @Positive(message = "Minimum price must be positive")
        Double minPrice,

        @Positive(message = "Maximum price must be positive")
        Double maxPrice,

        List<String> brands,

        @Min(value = 0, message = "Rating cannot be negative")
        Double minRating,

        List<String> storage,
        List<String> ram,

        @Min(value = 0, message = "Battery capacity cannot be negative")
        Integer battery,

        ProductCategory category
) {}
