package com.example.SmartShop.AI.Assistant.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Index;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

/**
 * Price history entity
 * Stores historical price changes for offers
 */
@Entity
@Table(
        name = "price_history",
        indexes = {
                @Index(name = "idx_price_history_offer", columnList = "offer_id"),
                @Index(name = "idx_price_history_checked_at", columnList = "checked_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    // Related offer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    // Recorded price
    @Column(nullable = false)
    private Double price;

    // Timestamp when price was checked
    @Column(nullable = false)
    private LocalDateTime checkedAt;
}
