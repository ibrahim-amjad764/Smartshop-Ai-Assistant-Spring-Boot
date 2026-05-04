package com.example.SmartShop.AI.Assistant.Dto;

import java.util.UUID;

/**
 * AI-style price comparison response
 */
public record PriceComparisonDTO(

        UUID productId,
        String productName,

        Double cheapestPrice,
        String cheapestStore,

        Double highestPrice,
        String highestStore,

        Double averagePrice,
        Double variationPercent,

        String recommendationMessage
) {
}