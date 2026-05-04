//package com.example.SmartShop.AI.Assistant.Dto;
//
//import lombok.Builder;
//import lombok.Getter;
//import lombok.ToString;
//
//import java.util.UUID;
//
///**
// * CartItemDTO represents an item in the shopping cart.
// * - Immutable for thread-safety.
// * - Builder pattern for readable object creation.
// * - Includes console logging for debugging purposes.
// */
//@Getter
//@ToString
//@Builder // Modern, flexible construction
//public class CartItemDTO {
//
//    private final UUID productId;
//    private final String title;
//    private final String brand;
//    private final String model;
//    private final int quantity;
//    private final double price; // Latest offer price
//
//    /**
//     * Factory method to create a CartItemDTO with logging.
//     * Performs basic validation.
//     */
//    public static CartItemDTO create(UUID productId, String title, String brand, String model, int quantity, double price) {
//        // Validate inputs
//        if (productId == null) {
//            System.out.println("Warning: productId is null");
//        }
//        if (quantity < 1) {
//            System.out.println("Warning: quantity is less than 1, defaulting to 1");
//            quantity = 1;
//        }
//        if (price < 0) {
//            System.out.println("Warning: price is negative, defaulting to 0");
//            price = 0.0;
//        }
//
//        CartItemDTO item = CartItemDTO.builder()
//                .productId(productId)
//                .title(title)
//                .brand(brand)
//                .model(model)
//                .quantity(quantity)
//                .price(price)
//                .build();
//
//        // Log creation
//        System.out.println("CartItemDTO created: " + item);
//
//        return item;
//    }
//}

package com.example.SmartShop.AI.Assistant.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * CartItemDTO is used to send cart info to frontend.
 * Contains product info and quantity.
 */
@Getter
@Setter
@AllArgsConstructor(access = lombok.AccessLevel.PUBLIC) // public constructor
public class CartItemDTO {

    @NotNull(message = "productId is required")
    private UUID productId;

    private String title;
    private String brand;
    private String model;
    private String image;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    private double price; // Latest offer price if needed
}

