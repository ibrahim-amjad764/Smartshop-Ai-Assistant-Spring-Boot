package com.example.SmartShop.AI.Assistant.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PriceHistoryDTO {

    private UUID offerId;
    private Double price;
    private LocalDateTime checkedAt;

    public PriceHistoryDTO() {}

    public PriceHistoryDTO(UUID offerId, Double price, LocalDateTime checkedAt) {
        this.offerId = offerId;
        this.price = price;
        this.checkedAt = checkedAt;
    }

    public UUID getOfferId() {
        return offerId;
    }

    public void setOfferId(UUID offerId) {
        this.offerId = offerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}