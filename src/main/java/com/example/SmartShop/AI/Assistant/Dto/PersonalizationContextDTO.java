package com.example.SmartShop.AI.Assistant.Dto;

import java.util.List;
import java.util.UUID;

public record PersonalizationContextDTO(
        UUID userId,
        List<UUID> favoriteProductIds,
        List<UUID> cartProductIds,
        List<String> recentSearches
) {}
