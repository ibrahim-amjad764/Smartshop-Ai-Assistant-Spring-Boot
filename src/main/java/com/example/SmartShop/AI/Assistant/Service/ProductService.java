//package com.example.SmartShop.AI.Assistant.Service;
//
//import com.example.SmartShop.AI.Assistant.Dto.ProductDTO;
//import com.example.SmartShop.AI.Assistant.Entity.Product;
//import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
//import org.springframework.stereotype.Service;
//import com.example.SmartShop.AI.Assistant.Specification.ProductSpecification;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class ProductService {
//
//    private final ProductRepository productRepository; // ✅ repository field
//
//    // ✅ Inject ProductRepository via constructor
//    public ProductService(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
//    // ✅ Fetch top 10 products by title (case-insensitive)
//    public List<Product> findTop10ByTitleContainingIgnoreCase(String title) {
//        return productRepository.findTop10ByTitleContainingIgnoreCase(title);
//    }
//
//    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
//// ✅ Logger for debugging & monitoring
//    /**
//     * Converts a single Product entity to ProductDTO
//     */
//    public ProductDTO toDto(Product product) {
//        Double minPrice = null;
//        if (product.getOffers() != null && !product.getOffers().isEmpty()) {
//            minPrice = product.getOffers().stream()
//                    .map(o -> o.getPrice())
//                    .min(Double::compareTo)
//                    .orElse(null);
//        }
//
//        return new ProductDTO(
//                product.getId(),
//                product.getTitle(),
//                product.getBrand(),
//                product.getModel(),
//                minPrice,       // lowestOfferPrice
//                product.getName(), // name (or null if not available)
//                product.getPrice() // price (or null if not available)
//        );
//    }
//
//    /**
//     * Converts a list of Product entities to a list of ProductDTOs
//     */
//    public List<ProductDTO> toDtoList(List<Product> products) {
//        return products.stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }
//    /**
//     * Dynamic Filter Method
//     *
//     * Purpose:
//     * Applies advanced filters using JPA Specification.
//     * Optimized, scalable and production-ready.
//     */
//    public List<ProductDTO> filterProducts(
//            Double minPrice,
//            Double maxPrice,
//            List<String> brand,
//            Double minRating,
//            List<String> storage,
//            List<String> ram,
//            Integer battery
//    ) {
//
//        logger.info("Applying product filters...");
//        logger.info("MinPrice: {}, MaxPrice: {}, Brand: {}, Rating: {}, Storage: {}, RAM: {}, Battery: {}",
//                minPrice, maxPrice, brand, minRating, storage, ram, battery);
//
//        // ✅ Build dynamic Specification
//        Specification<Product> specification =
//                ProductSpecification.filterProducts(
//                        minPrice, maxPrice, brand, minRating, storage, ram, battery
//                );
//
//        // ✅ Execute filtered query
//        List<Product> filteredProducts = productRepository.findAll(specification);
//
//        logger.info("Filtered product count: {}", filteredProducts.size());
//
//        return toDtoList(filteredProducts);
//    }
//}
package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.ProductDTO;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Entity.ProductCategory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ==============================================================
 * ProductService (Interface)
 * ==============================================================
 * Purpose:
 * Defines all product-related business operations.
 *
 * Architecture Notes:
 * - Keeps Controller thin (only request handling)
 * - Moves all logic to Service layer (best practice)
 * - DTO mapping is centralized here for consistency
 *
 * 🔥 IMPORTANT DESIGN DECISION:
 * Price MUST be derived from Offer entity (NOT Product.price)
 * Implementations of toDto() should:
 *   - Calculate lowestOfferPrice using MIN(offer.price)
 *   - Set DTO.price using fallback logic
 *
 * Logging Strategy:
 * - All implementations MUST include System.out logs
 * - Log inputs, outputs, and edge cases (nulls, empty lists)
 *
 * Performance Notes:
 * - Avoid N+1 queries when fetching offers
 * - Prefer JOIN FETCH or batch fetching
 * ==============================================================
 */
public interface ProductService {

    /**
     * ==========================================================
     * CREATE PRODUCT
     * ==========================================================
     * Creates a new Product with optional image upload.
     *
     * Responsibilities:
     * - Validate DTO fields
     * - Upload image to Cloudinary (if provided)
     * - Persist product in DB
     *
     * Logging (Implementation MUST include):
     * System.out.println("[ProductService] Creating product -> " + dto.getTitle());
     *
     * @param dto   ProductDTO containing product data
     * @param image Optional image file
     * @return Saved Product entity
     */
    Product createProduct(ProductDTO dto, MultipartFile image);

    /**
     * ==========================================================
     * SEARCH PRODUCTS (Autocomplete)
     * ==========================================================
     * Returns top 10 products matching title (case-insensitive).
     *
     * Optimization Tip:
     * - Ensure DB index on LOWER(title)
     *
     * Logging:
     * System.out.println("[ProductService] Searching products -> " + title);
     *
     * @param title search keyword
     * @return max 10 products
     */
    List<Product> findTop10ByTitleContainingIgnoreCase(String title);

    /**
     * ==========================================================
     * ENTITY → DTO MAPPING (CRITICAL METHOD)
     * ==========================================================
     * Converts Product entity into ProductDTO.
     *
     *  MUST DO IN IMPLEMENTATION:
     * - Extract offers
     * - Filter available offers
     * - Compute:
     *
     *   Double lowestPrice = product.getOffers().stream()
     *      .filter(o -> Boolean.TRUE.equals(o.getAvailable()))
     *      .map(o -> o.getPrice())
     *      .min(Double::compareTo)
     *      .orElse(null);
     *
     * - Pass lowestPrice into DTO
     *
     * Logging:
     * System.out.println("[ProductService] Mapping product -> " + product.getId());
     *
     * @param product entity
     * @return ProductDTO
     */
    ProductDTO toDto(Product product);

    /**
     * ==========================================================
     * BULK ENTITY → DTO CONVERSION
     * ==========================================================
     *
     * Best Practice:
     * - Use stream mapping
     * - Avoid repeated DB calls inside loop
     *
     * Logging:
     * System.out.println("[ProductService] Converting product list size = " + products.size());
     *
     * @param products list of entities
     * @return list of DTOs
     */
    List<ProductDTO> toDtoList(List<Product> products);

    /**
     * ==========================================================
     * DYNAMIC PRODUCT FILTERING
     * ==========================================================
     * Supports multiple optional filters.
     *
     * Real-world Use:
     * - E-commerce filters (price range, specs, brand)
     *
     *  IMPORTANT:
     * - Price filtering MUST use lowestOfferPrice (NOT product.price)
     * - Use Specification API efficiently
     *
     * Logging:
     * System.out.println("[ProductService] Filtering products...");
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param brand list of brands
     * @param minRating rating filter
     * @param storage storage filter
     * @param ram ram filter
     * @param battery battery filter
     * @return filtered DTO list
     */

    List<ProductDTO> filterProducts(
            Double minPrice,
            Double maxPrice,
            List<String> brand,
            Double minRating,
            List<String> storage,
            List<String> ram,
            Integer battery,
            ProductCategory category
    );


    /**
     * ==========================================================
     * FEATURED PRODUCTS
     * ==========================================================
     *
     * Use Case:
     * - Homepage / recommendations
     *
     * Optimization:
     * - Cache results if static
     *
     * Logging:
     * System.out.println("[ProductService] Fetching featured products");
     *
     * @return featured product list
     */
    List<Product> getFeaturedProducts();

    // ==========================================================
    // PRODUCT IMAGE MANAGEMENT
    // ==========================================================

    /**
     * Updates single product image.
     *
     * Safety:
     * - Delete old image (if exists)
     * - Upload new image
     *
     * Logging:
     * System.out.println("[ProductService] Updating image for product -> " + productId);
     *
     * @param productId product UUID
     * @param imageFile image file
     * @return updated product
     */
    Product updateProductImage(UUID productId, MultipartFile imageFile) throws IOException;

    /**
     * Batch image update (transaction-safe).
     *
     *  IMPORTANT:
     * - If one upload fails → rollback ALL
     * - Prevent partial updates
     *
     * Logging:
     * System.out.println("[ProductService] Batch image update size = " + productImages.size());
     *
     * @param productImages map of productId → image
     * @return updated products
     */
    List<Product> updateMultipleProductImages(Map<UUID, MultipartFile> productImages) throws IOException;
}