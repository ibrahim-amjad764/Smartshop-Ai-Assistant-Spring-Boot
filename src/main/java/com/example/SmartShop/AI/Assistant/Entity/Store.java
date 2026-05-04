package com.example.SmartShop.AI.Assistant.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Store entity
 * Represents an e-commerce store or marketplace
 */
@Entity
@Table(
        name = "stores",
        indexes = {
                @Index(name = "idx_store_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue // Hibernate 6 automatically generates UUID for UUID-typed IDs
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    // Unique store display name (e.g. Daraz, Amazon)
    @Column(nullable = false, length = 150, unique = true)
    private String name;

    // API or scraping base URL
    @Column(length = 500)
    private String apiUrl;

    @Column(name = "img_url", length = 500)
    private String logoUrl;

    // List of offers from this store
    @OneToMany(
            mappedBy = "store",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Offer> offers;
}

