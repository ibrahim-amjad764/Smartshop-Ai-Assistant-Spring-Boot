package com.example.SmartShop.AI.Assistant.Dto;

import java.util.UUID;

public record StoreDetailDTO(
        UUID id,
        String name,
        String logoUrl,
        String apiUrl,
        StoreRatingDTO rating
) {}
