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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Offer entity
 * Represents a product listing from a specific store
 */
@Entity
@Table(name = "offer",
        indexes = {
                @Index(name = "idx_offer_product", columnList = "product_id"),
                @Index(name = "idx_offer_store", columnList = "store_id"),
                @Index(name = "idx_offer_price", columnList = "price"),
                @Index(name = "idx_offer_available", columnList = "available")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    // Product to which this offer belongs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("offers") // Ignore offers when serializing Product to avoid recursion
    private Product product;

    // Store offering the product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // Offer price
    @Column(nullable = false)
    private Double price;

    // Product page URL
    @Column(length = 500)
    private String url;

    // Availability status
    @Column(nullable = false)
    private Boolean available;

    // Timestamp when this offer was fetched
    @Column(nullable = false)
    private LocalDateTime fetchedAt;
}