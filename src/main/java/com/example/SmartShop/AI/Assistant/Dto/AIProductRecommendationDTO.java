package com.example.SmartShop.AI.Assistant.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for AI-powered product recommendations.
 * Includes product details + AI reasoning.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIProductRecommendationDTO {

    private UUID productId;
    private String title;
    private String brand;
    private Double price;
    private String imageUrl;

    // AI-generated fields
    private Integer aiScore;        // 1-10 relevance score
    private String aiReason;        // Why this product fits the query

    // Optional: Additional product specs
    private String model;
    private String ram;
    private String storage;
}