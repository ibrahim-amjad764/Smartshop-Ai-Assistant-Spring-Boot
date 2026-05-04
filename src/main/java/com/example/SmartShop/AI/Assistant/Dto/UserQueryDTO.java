package com.example.SmartShop.AI.Assistant.Dto;

import lombok.Data;

/**
 * AI extracted structured user intent
 * (Phase 2 - AI Query Understanding Layer)
 */
@Data
public class UserQueryDTO {

    private Integer budget;      // extracted price limit
    private String category;     // laptop, phone, etc.
    private String usage;        // gaming, office, study
    private String preferences;  // extra requirements / quality hints
}