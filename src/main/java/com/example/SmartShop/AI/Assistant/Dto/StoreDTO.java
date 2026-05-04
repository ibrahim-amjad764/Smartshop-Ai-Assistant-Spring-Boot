package com.example.SmartShop.AI.Assistant.Dto;

import java.util.UUID;

/**
 * DTO for exposing minimal store info to frontend.
 */
public record StoreDTO(
        UUID id,   // matches Store entity
        String name
) {}