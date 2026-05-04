package com.example.SmartShop.AI.Assistant.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for updating cart item quantity.
 *
 * Purpose:
 * - Used in PATCH/PUT cart update endpoint.
 * - Ensures quantity is valid before hitting service layer.
 *
 * Design Notes:
 * - @NoArgsConstructor → Required by Jackson (JSON deserialization)
 * - @AllArgsConstructor → Useful for testing
 * - Uses wrapper Integer for better validation handling
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuantityRequestDTO {

    /**
     * Quantity must:
     * - Not be null
     * - Be at least 1
     *
     * Using Integer instead of int allows @NotNull to work properly.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}