package com.example.SmartShop.AI.Assistant.Dto;

import java.util.List;

/**
 * Response returned from search endpoint.
 * Contains offers + AI suggestion
 */
public record SearchResponseDTO(
        List<OfferDTO> offers,
        String aiSuggestion
) {
}