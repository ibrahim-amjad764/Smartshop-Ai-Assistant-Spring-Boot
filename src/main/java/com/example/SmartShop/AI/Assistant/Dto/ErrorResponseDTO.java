package com.example.SmartShop.AI.Assistant.Dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        String message,
        String internalServerError,
        int status,
        LocalDateTime timestamp
) {}