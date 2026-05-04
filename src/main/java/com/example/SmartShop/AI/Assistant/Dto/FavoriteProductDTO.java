package com.example.SmartShop.AI.Assistant.Dto;

import com.example.SmartShop.AI.Assistant.Entity.Product;

import java.util.List;
import java.util.UUID;

/**
 * DTO for sending favorite product info to frontend.
 * Avoids exposing JPA entities directly and prevents circular references.
 * Includes computed pricing from offers (effectivePrice, lowestOfferPrice)
 */
public class FavoriteProductDTO {

    private UUID id;
    private String title;
    private String brand;
    private String model;
    private List<OfferDTO> offers;
    private String imageUrl;
    private Double effectivePrice;    //  Computed price from offers
    private Double lowestOfferPrice;  //  Lowest available offer price

    public FavoriteProductDTO() {
    }

    /**
     * Full constructor with computed pricing fields
     * @param id Product UUID
     * @param title Product title
     * @param brand Product brand
     * @param model Product model
     * @param imageUrl Product image URL
     * @param effectivePrice Computed effective price (lowest offer or product price)
     * @param lowestOfferPrice Lowest price from available offers
     * @param offers List of offer DTOs
     */
    public FavoriteProductDTO(UUID id, String title, String brand, String model,
                              String imageUrl, Double effectivePrice,
                              Double lowestOfferPrice, List<OfferDTO> offers) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.imageUrl = imageUrl;
        this.effectivePrice = effectivePrice;
        this.lowestOfferPrice = lowestOfferPrice;
        this.offers = offers;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<OfferDTO> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferDTO> offers) {
        this.offers = offers;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getEffectivePrice() {
        return effectivePrice;
    }

    public void setEffectivePrice(Double effectivePrice) {
        this.effectivePrice = effectivePrice;
    }

    public Double getLowestOfferPrice() {
        return lowestOfferPrice;
    }

    public void setLowestOfferPrice(Double lowestOfferPrice) {
        this.lowestOfferPrice = lowestOfferPrice;
    }

    /**
     * Null-safe mapping from Product entity to DTO
     * Converts Product → FavoriteProductDTO with nested OfferDTOs
     * Note: Use FavoriteService.mapToDTO() for full price computation
     */
    public static FavoriteProductDTO fromEntity(Product product) {
        List<OfferDTO> offerDTOs = product.getOffers() != null
                ? product.getOffers().stream()
                .map(o -> {
                    UUID storeId = o.getStore() != null ? o.getStore().getId() : null;
                    String storeName = o.getStore() != null ? o.getStore().getName() : null;

                    return new OfferDTO(
                            o.getId(),
                            o.getPrice(),
                            o.getAvailable(),
                            o.getUrl(),
                            o.getFetchedAt(),
                            product.getId(),
                            product.getTitle(),
                            storeId,
                            storeName
                    );
                }).toList()
                : List.of();

        // Simple mapping without price computation - use FavoriteService.mapToDTO() for full logic
        return new FavoriteProductDTO(
                product.getId(),
                product.getTitle(),
                product.getBrand(),
                product.getModel(),
                product.getImageUrl(),
                product.getPrice(),     //  May be null - prefer Service computation
                null,                   //  Not computed here
                offerDTOs);
    }
}