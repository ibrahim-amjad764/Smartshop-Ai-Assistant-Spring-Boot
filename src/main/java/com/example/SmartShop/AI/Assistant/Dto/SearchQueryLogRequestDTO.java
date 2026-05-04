package com.example.SmartShop.AI.Assistant.Dto;

public record SearchQueryLogRequestDTO(
        String queryText,
        Double userBudget
) {}
