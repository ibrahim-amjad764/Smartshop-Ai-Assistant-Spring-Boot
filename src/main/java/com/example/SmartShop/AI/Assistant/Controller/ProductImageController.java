package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.UUID;

/**
 * Product Image Controller
 * REST API endpoints for safe batch image updates with Cloudinary
 */
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ProductImageController {

    private final ProductService productService;

    @Autowired
    public ProductImageController(ProductService productService) {
        this.productService = productService;
        System.out.println("[ProductImageController]  Product image controller initialized");
    }

    // =========================
    // Update single product image
    // =========================
    @PostMapping("/{productId}/image")
    public ResponseEntity<?> updateProductImage(
            @PathVariable UUID productId,
            @RequestParam("image") MultipartFile imageFile) {

        System.out.println("[ProductImageController]  Received image upload for product: " + productId);

        try {
            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Image file cannot be empty"));
            }

            Product updatedProduct = productService.updateProductImage(productId, imageFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product image updated successfully");
            response.put("product", updatedProduct);
            response.put("imageUrl", updatedProduct.getImageUrl());

            System.out.println("[ProductImageController]  Image updated successfully for product: " + productId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("[ProductImageController]  Product not found: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            System.err.println("[ProductImageController]  Image upload failed: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("[ProductImageController]  Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Internal server error"));
        }
    }

    // =========================
    // Update multiple product images
    // =========================
    @PostMapping("/batch-images")
    public ResponseEntity<?> updateMultipleProductImages(
            @RequestParam Map<String, MultipartFile> files) {

        System.out.println("[ProductImageController]  Received batch image update for " + files.size() + " products");

        try {
            Map<UUID, MultipartFile> productImages = new HashMap<>();

            for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                try {
                    UUID productId = UUID.fromString(entry.getKey());
                    MultipartFile imageFile = entry.getValue();
                    if (imageFile != null && !imageFile.isEmpty()) {
                        productImages.put(productId, imageFile);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("[ProductImageController]  Invalid UUID: " + entry.getKey());
                }
            }

            if (productImages.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("No valid image files provided"));
            }

            List<Product> updatedProducts = productService.updateMultipleProductImages(productImages);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully updated " + updatedProducts.size() + " product images");
            response.put("updatedProducts", updatedProducts);
            response.put("count", updatedProducts.size());

            System.out.println("[ProductImageController]  Batch update completed: " + updatedProducts.size() + " products");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.err.println("[ProductImageController]  Batch upload failed: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Batch upload failed: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("[ProductImageController]  Unexpected batch error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Internal server error during batch update"));
        }
    }

    // =========================
    // Helper: Standard error response
    // =========================
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }

}