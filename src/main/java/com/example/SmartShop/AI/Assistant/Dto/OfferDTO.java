package com.example.SmartShop.AI.Assistant.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for sending offer info to frontend.
 * Immutable record prevents circular references.
 */
public record OfferDTO(
        UUID id,             // Offer ID
        Double price,
        Boolean available,
        String url,
        LocalDateTime fetchedAt,
        UUID productId,      // UUID matches Product entity
        String productTitle,
        UUID storeId,        //  UUID now matches Store entity
        String storeName
) {
}