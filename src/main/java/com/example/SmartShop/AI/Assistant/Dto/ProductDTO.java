package com.example.SmartShop.AI.Assistant.Dto;

import com.example.SmartShop.AI.Assistant.Entity.ProductCategory;

import java.util.UUID;

/**
 * ==============================================================
 * ProductDTO
 * ==============================================================
 * Purpose:
 * Data Transfer Object used to send product data from backend
 * to frontend without exposing the full Product entity.
 *
 * Enhancements:
 * - Null safety improvements
 * - Consistent price resolution logic
 * - Debug-friendly logging
 * - Production-safe fallbacks
 * ==============================================================
 */
public class ProductDTO {

    private UUID id;
    private String title;
    private String brand;
    private String model;
    private Double lowestOfferPrice;
    private String imageUrl;
    private Double price;
    private Double rating;
    private String storage;
    private String ram;
    private Integer battery;
    private String camera;
    private String display;
    private String processor;

    // ADD
    private ProductCategory category;

    // Laptop
    private String graphicsCard;
    private String displaySize;

    // Headphone
    private String headphoneType;
    private String connectivity;
    private String batteryLife;
    private Boolean noiseCancellation;
    private String driverSize;

    /**
     * ==========================================================
     * Constructor
     * ==========================================================
     * Used by service layer when mapping Product -> ProductDTO
     */
    public ProductDTO(UUID id, String title, String brand, String model,
                      Double lowestOfferPrice, String imageUrl, Double price) {

        this.id = id;
        this.title = title;
        this.brand = brand;
        this.model = model;

        //  Normalize price at creation time
        this.lowestOfferPrice = lowestOfferPrice;
        this.price = price != null ? price : lowestOfferPrice;

        //  Safe image handling
        if (imageUrl == null || imageUrl.isBlank()) {
            System.out.println("[ProductDTO] ️ Missing image, setting NULL (frontend should fallback)");
            this.imageUrl = null;
        } else {
            this.imageUrl = imageUrl;
        }

        // Debug logging
        System.out.println("[ProductDTO] Created DTO -> "
                + "id=" + id
                + ", title=" + title
                + ", price=" + this.price
                + ", lowestOfferPrice=" + lowestOfferPrice);
    }

    // ==========================================================
    // GETTERS
    // ==========================================================

    public UUID getId() { return id; }

    public String getTitle() { return title; }

    public String getBrand() { return brand; }

    public String getModel() { return model; }

    public Double getLowestOfferPrice() { return lowestOfferPrice; }

    public String getImageUrl() { return imageUrl; }

    public Double getRating() { return rating; }

    public String getStorage() { return storage; }

    public String getRam() { return ram; }

    public Integer getBattery() { return battery; }

    public String getCamera() { return camera; }

    public String getDisplay() { return display; }

    public String getProcessor() { return processor; }

    /**
     * ==========================================================
     * getPrice()
     * ==========================================================
     * Always returns a usable price for frontend.
     *
     * Priority:
     * 1. price
     * 2. lowestOfferPrice
     *
     * Logs fallback usage for debugging.
     */
    public Double getPrice() {

        if (price != null) {
            return price;
        }

        if (lowestOfferPrice != null) {
            System.out.println("[ProductDTO] ⚡ Using fallback lowestOfferPrice -> " + lowestOfferPrice);
            return lowestOfferPrice;
        }

        System.out.println("[ProductDTO]  No price available");
        return null;
    }

    // ==========================================================
    // SETTERS
    // ==========================================================

    public void setPrice(Double price) {

        System.out.println("[ProductDTO] setPrice called -> " + price);

        this.price = price;

        //  Auto-sync lowestOfferPrice if missing
        if (this.lowestOfferPrice == null && price != null) {
            System.out.println("[ProductDTO] Syncing lowestOfferPrice with price");
            this.lowestOfferPrice = price;
        }
    }

    public void setLowestOfferPrice(Double lowestOfferPrice) {

        System.out.println("[ProductDTO] setLowestOfferPrice -> " + lowestOfferPrice);

        this.lowestOfferPrice = lowestOfferPrice;

        //  Auto-sync price if missing
        if (this.price == null && lowestOfferPrice != null) {
            System.out.println("[ProductDTO] Syncing price with lowestOfferPrice");
            this.price = lowestOfferPrice;
        }
    }

    public void setRating(Double rating) {
        System.out.println("[ProductDTO] setRating -> " + rating);
        this.rating = rating;
    }

    public void setStorage(String storage) {
        System.out.println("[ProductDTO] setStorage -> " + storage);
        this.storage = storage;
    }

    public void setRam(String ram) {
        System.out.println("[ProductDTO] setRam -> " + ram);
        this.ram = ram;
    }

    public void setBattery(Integer battery) {
        System.out.println("[ProductDTO] setBattery -> " + battery);
        this.battery = battery;
    }

    public void setCamera(String camera) {
        System.out.println("[ProductDTO] setCamera -> " + camera);
        this.camera = camera;
    }

    public void setDisplay(String display) {
        System.out.println("[ProductDTO] setDisplay -> " + display);
        this.display = display;
    }

    public void setProcessor(String processor) {
        System.out.println("[ProductDTO] setProcessor -> " + processor);
        this.processor = processor;
    }
    // ==========================
// NEW GETTERS
// ==========================

    public ProductCategory getCategory() {
        return category;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public String getDisplaySize() {
        return displaySize;
    }

    public String getHeadphoneType() {
        return headphoneType;
    }

    public String getConnectivity() {
        return connectivity;
    }

    public String getBatteryLife() {
        return batteryLife;
    }

    public Boolean getNoiseCancellation() {
        return noiseCancellation;
    }

    public String getDriverSize() {
        return driverSize;
    }


// ==========================
// NEW SETTERS
// ==========================

    public void setCategory(ProductCategory category) {
        System.out.println("[ProductDTO] setCategory -> " + category);
        this.category = category;
    }

    public void setGraphicsCard(String graphicsCard) {
        System.out.println("[ProductDTO] setGraphicsCard -> " + graphicsCard);
        this.graphicsCard = graphicsCard;
    }

    public void setDisplaySize(String displaySize) {
        System.out.println("[ProductDTO] setDisplaySize -> " + displaySize);
        this.displaySize = displaySize;
    }

    public void setHeadphoneType(String headphoneType) {
        System.out.println("[ProductDTO] setHeadphoneType -> " + headphoneType);
        this.headphoneType = headphoneType;
    }

    public void setConnectivity(String connectivity) {
        System.out.println("[ProductDTO] setConnectivity -> " + connectivity);
        this.connectivity = connectivity;
    }

    public void setBatteryLife(String batteryLife) {
        System.out.println("[ProductDTO] setBatteryLife -> " + batteryLife);
        this.batteryLife = batteryLife;
    }

    public void setNoiseCancellation(Boolean noiseCancellation) {
        System.out.println("[ProductDTO] setNoiseCancellation -> " + noiseCancellation);
        this.noiseCancellation = noiseCancellation;
    }

    public void setDriverSize(String driverSize) {
        System.out.println("[ProductDTO] setDriverSize -> " + driverSize);
        this.driverSize = driverSize;
    }

    // ==========================================================
    // HELPER METHODS
    // ==========================================================

    /**
     * Returns the best available price
     * Safe for frontend consumption
     */
    public Double getEffectivePrice() {

        Double effectivePrice = (price != null) ? price : lowestOfferPrice;

        System.out.println("[ProductDTO] getEffectivePrice -> " + effectivePrice);

        return effectivePrice;
    }

    /**
     * Indicates if product has valid pricing
     */
    public boolean hasValidPrice() {

        boolean valid = (price != null || lowestOfferPrice != null);

        System.out.println("[ProductDTO] hasValidPrice -> " + valid);

        return valid;
    }

    /**
     * Debug helper
     */
    public void debug() {

        System.out.println("================================");
        System.out.println(" ProductDTO DEBUG INFO ");
        System.out.println("================================");
        System.out.println("ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Brand: " + brand);
        System.out.println("Model: " + model);
        System.out.println("Price: " + price);
        System.out.println("Lowest Offer Price: " + lowestOfferPrice);
        System.out.println("Effective Price: " + getEffectivePrice());
        System.out.println("Image URL: " + imageUrl);
        System.out.println("Has Valid Price: " + hasValidPrice());
        System.out.println("================================");
    }
}