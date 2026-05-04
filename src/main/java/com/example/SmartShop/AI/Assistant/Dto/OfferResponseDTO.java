package com.example.SmartShop.AI.Assistant.Dto;

import java.util.UUID;

public record OfferResponseDTO(
        UUID offerId,
        UUID storeId,
        String storeName,
        String storeLogoUrl,
        Double price,
        Boolean availability,
        String productUrl,
        boolean isCheapest
) {}
