package com.example.SmartShop.AI.Assistant.Dto;

import java.util.UUID;

public record StoreSummaryDTO(
        UUID id,
        String name,
        String logoUrl
) {}
