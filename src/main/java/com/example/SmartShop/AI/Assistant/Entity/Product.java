package com.example.SmartShop.AI.Assistant.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
// ADD THIS IMPORT
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_title", columnList = "title"),
                @Index(name = "idx_product_brand", columnList = "brand"),
                @Index(name = "idx_product_rating", columnList = "rating"),
                @Index(name = "idx_product_storage", columnList = "storage"),
                @Index(name = "idx_product_ram", columnList = "ram"),
                @Index(name = "idx_product_battery", columnList = "battery")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Product {

    // ==============================
    // PRIMARY KEY
    // ==============================
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    /**
     * Product category (PHONE, LAPTOP, HEADPHONE)
     * Used for filtering + conditional logic
     */
    @Enumerated(EnumType.STRING)
    private ProductCategory category;


    // ==============================
    // BASIC PRODUCT INFO
    // ==============================
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(name = "image_url")
    private String imageUrl;

    // ==============================
    // PRICE
    // ==============================
    @Column
    private Double price;  //  Add this field for product price

    // ==============================
    // Timestamps
    // ==============================
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==============================
    // FILTERABLE SPECIFICATIONS
    // ==============================
    @Column
    private Double rating;

    @Column(length = 50)
    private String storage;

    @Column(length = 50)
    private String ram;

    @Column
    private Integer battery;

    // ==============================
    // ADDITIONAL SPECIFICATIONS
   // ==============================

    @Column(length = 100)
    private String camera;

    @Column(length = 100)
    private String display;

    @Column(length = 100)
    private String processor;

    // ==============================
    // RELATIONSHIPS
    // ==============================
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Offer> offers;

    // Flag for featured products
    @Column(nullable = false)
    private Boolean featured = false;

    // ==============================
    // LIFECYCLE HOOKS
    // ==============================
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


// ================= LAPTOP SPECS =================

    /**
     * GPU info (RTX 4060 / Integrated)
     */
    @Column(length = 100)
    private String graphicsCard;

    /**
     * Screen size (15.6", 14")
     */
    @Column(length = 50)
    private String displaySize;


// ================= HEADPHONE SPECS =================

    /**
     * Type: Over-ear / In-ear / On-ear
     */
    @Column(length = 50)
    private String headphoneType;

    /**
     * Connectivity: Wired / Wireless / Bluetooth
     */
    @Column(length = 50)
    private String connectivity;

    /**
     * Battery backup (e.g., 30 hours)
     */
    @Column(length = 50)
    private String batteryLife;

    /**
     * Active Noise Cancellation support
     */
    private Boolean noiseCancellation;

    /**
     * Driver size (e.g., 40mm)
     */
    @Column(length = 50)
    private String driverSize;

    // ==============================
    // CONSTRUCTORS
    // ==============================
    public Product(String title, String brand, String model, String imageUrl, Double price) {
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.imageUrl = imageUrl;
        this.price = price;  //  set price

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        System.out.println("=== PRODUCT TYPE CHECK ===");
        System.out.println("Category: " + this.category);

        if (this.category == ProductCategory.LAPTOP) {
            System.out.println("GPU: " + this.graphicsCard);
            System.out.println("Display Size: " + this.displaySize);
        }

        if (this.category == ProductCategory.HEADPHONE) {
            System.out.println("Type: " + this.headphoneType);
            System.out.println("Noise Cancellation: " + this.noiseCancellation);
        }

    }

    // ==============================
// IMAGE HELPER METHOD
// Purpose: Provide consistent image getter for services/DTO mapping
// Returns imageUrl safely with fallback
// ==============================
    // In Product.java

    // In Product.java

    public String getImage() {
        System.out.println(" [Product] getImage() called for product ID=" + this.id);

        if (this.imageUrl != null && !this.imageUrl.isBlank()) {
            if (this.imageUrl.startsWith("http://") || this.imageUrl.startsWith("https://")) {
                System.out.println(" [Product] Absolute imageUrl detected, returning: " + this.imageUrl);
                return this.imageUrl;
            }

            String backendBaseUrl = "http://localhost:8080"; // your backend URL
            String normalized = this.imageUrl.startsWith("/") ? backendBaseUrl + this.imageUrl : backendBaseUrl + "/" + this.imageUrl;
            System.out.println(" [Product] Relative imageUrl normalized to: " + normalized);
            return normalized;
        }

        // Return full URL to frontend placeholder image instead of relative path
        String frontendBaseUrl = "http://localhost:3000"; // React dev server URL
        String placeholderUrl = frontendBaseUrl + "/placeholder-product.jpg";

        System.out.println(" [Product] No image found, returning frontend placeholder: " + placeholderUrl);
        return placeholderUrl;
    }

}
