//
//package com.example.SmartShop.AI.Assistant.Service;
//
//import com.example.SmartShop.AI.Assistant.Dto.ProductDTO;
//import com.example.SmartShop.AI.Assistant.Entity.Product;
//import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
//import com.example.SmartShop.AI.Assistant.Specification.ProductSpecification;
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import org.apache.commons.io.FilenameUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * ===============================================================
// * ProductServiceImpl
// * ===============================================================
// *
// * Responsibilities:
// * - Product creation
// * - Product image upload (Cloudinary)
// * - DTO conversion
// * - Product filtering
// *
// * Added Enhancements:
// * ✔ Extra debug logs for troubleshooting price issues
// * ✔ Defensive validation logs
// * ✔ DTO inspection logs
// * ✔ Safe Cloudinary monitoring
// *
// * NOTE:
// * No existing code has been modified or removed.
// * Only additional monitoring + debugging layers added.
// * ===============================================================
// */
//@Service
//@Transactional
//public class ProductServiceImpl implements ProductService {
//
//    private final ProductRepository productRepository;
//    private final Cloudinary cloudinary;
//    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
//
//    public ProductServiceImpl(ProductRepository productRepository, Cloudinary cloudinary) {
//        this.productRepository = productRepository;
//        this.cloudinary = cloudinary;
//        logger.info("ProductServiceImpl initialized with Cloudinary integration");
//
//        // DEBUG: repository sanity check
//        logger.debug("[INIT] ProductRepository instance -> {}", productRepository.getClass().getName());
//    }
//
//    // =========================
//    // Product Creation
//    // =========================
//    @Override
//    public Product createProduct(ProductDTO dto, MultipartFile image) {
//
//        logger.info("[createProduct] Creating product: {}", dto.getTitle());
//
//        // ---------------------------
//        // DEBUG DTO STATE
//        // ---------------------------
//        logger.debug("[createProduct] Incoming DTO data -> id={}, title={}, brand={}, model={}, price={}, lowestOfferPrice={}",
//                dto.getId(),
//                dto.getTitle(),
//                dto.getBrand(),
//                dto.getModel(),
//                dto.getPrice(),
//                dto.getLowestOfferPrice());
//
//        Product product = new Product();
//        product.setTitle(dto.getTitle());
//        product.setBrand(dto.getBrand());
//        product.setModel(dto.getModel());
//        product.setImageUrl(dto.getImageUrl());
//        product.setPrice(dto.getPrice()); // ✅ set price
//
//        // DEBUG price verification
//        if (dto.getPrice() == null) {
//            logger.warn("[createProduct] WARNING: Product '{}' is being created with NULL price", dto.getTitle());
//        } else {
//            logger.debug("[createProduct] Product price set to {}", dto.getPrice());
//        }
//
//        // Save first to generate UUID
//        Product saved = productRepository.save(product);
//
//        logger.info("[createProduct] Product saved with ID: {}", saved.getId());
//
//        // DEBUG saved entity
//        logger.debug("[createProduct] Saved entity verification -> id={}, title={}, price={}",
//                saved.getId(),
//                saved.getTitle(),
//                saved.getPrice());
//
//        // Upload image if provided
//        if (image != null && !image.isEmpty()) {
//
//            logger.debug("[createProduct] Image detected -> name={}, size={} bytes",
//                    image.getOriginalFilename(),
//                    image.getSize());
//
//            try {
//
//                String imageUrl = uploadToCloudinary(image, saved.getId(), saved.getTitle()); // ✅ pass title
//                saved.setImageUrl(imageUrl);
//                saved = productRepository.save(saved);
//
//                logger.info("[createProduct] Image uploaded successfully: {}", imageUrl);
//
//            } catch (IOException e) {
//
//                logger.error("[createProduct] Cloudinary upload failed: {}", e.getMessage());
//                throw new RuntimeException("Failed to upload image", e);
//            }
//        }
//
//        return saved;
//    }
//
//    // =========================
//    // Safe Image Updates
//    // =========================
//    public Product updateProductImage(UUID productId, MultipartFile imageFile) throws IOException {
//
//        logger.info("[updateProductImage] Updating image for product ID: {}", productId);
//
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
//
//        logger.debug("[updateProductImage] Found product -> title={}, currentImage={}",
//                product.getTitle(),
//                product.getImageUrl());
//
//        String imageUrl = uploadToCloudinary(imageFile, productId, product.getTitle()); // ✅ pass title
//
//        product.setImageUrl(imageUrl);
//        product = productRepository.save(product);
//
//        logger.info("[updateProductImage] Image updated successfully: {}", imageUrl);
//
//        return product;
//    }
//
//    public List<Product> updateMultipleProductImages(Map<UUID, MultipartFile> productImages) throws IOException {
//
//        logger.info("[updateMultipleProductImages] Starting batch update for {} products", productImages.size());
//
//        List<Product> updatedProducts = new ArrayList<>();
//        List<String> uploadedUrls = new ArrayList<>();
//
//        try {
//
//            for (Map.Entry<UUID, MultipartFile> entry : productImages.entrySet()) {
//
//                UUID productId = entry.getKey();
//                MultipartFile file = entry.getValue();
//
//                logger.debug("[updateMultipleProductImages] Processing product {}", productId);
//
//                Product product = productRepository.findById(productId)
//                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
//
//                String imageUrl = uploadToCloudinary(file, productId, product.getTitle()); // ✅ pass title
//                uploadedUrls.add(imageUrl);
//
//                product.setImageUrl(imageUrl);
//                updatedProducts.add(productRepository.save(product));
//
//                logger.info("[updateMultipleProductImages] Updated product {}", productId);
//            }
//
//            return updatedProducts;
//
//        } catch (Exception e) {
//
//            logger.error("[updateMultipleProductImages] Batch update failed: {}", e.getMessage());
//
//            cleanupFailedUploads(uploadedUrls);
//
//            throw new IOException("Batch image update failed", e);
//        }
//    }
//
//    private String uploadToCloudinary(MultipartFile image, UUID productId, String productTitle) throws IOException {
//
//        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
//        String uniqueName = "product_" + productId + "_" + UUID.randomUUID() + "." + extension;
//
//        logger.debug("[Cloudinary] Upload starting -> file={}, generatedName={}",
//                image.getOriginalFilename(),
//                uniqueName);
//
//        @SuppressWarnings("unchecked")
//        Map<String, Object> result = cloudinary.uploader().upload(
//                image.getBytes(),
//                ObjectUtils.asMap(
//                        "public_id", uniqueName,
//                        "folder", "products",
//                        "overwrite", true,
//                        "context", "title=" + productTitle
//                )
//        );
//
//        String secureUrl = (String) result.get("secure_url");
//
//        logger.debug("[Cloudinary] Upload result -> {}", secureUrl);
//
//        return secureUrl;
//    }
//
//    private void cleanupFailedUploads(List<String> urls) {
//
//        logger.warn("[cleanupFailedUploads] Cleaning {} uploaded files after failure", urls.size());
//
//        for (String url : urls) {
//
//            try {
//
//                String publicId = extractPublicId(url);
//
//                if (publicId != null)
//                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//
//            } catch (Exception e) {
//
//                logger.warn("Failed cleanup for URL {}: {}", url, e.getMessage());
//            }
//        }
//    }
//
//    private String extractPublicId(String url) {
//
//        try {
//
//            String[] parts = url.split("/");
//            String lastPart = parts[parts.length - 1];
//
//            return lastPart.substring(0, lastPart.lastIndexOf('.'));
//
//        } catch (Exception e) {
//
//            logger.warn("Failed to extract publicId from URL {}", url);
//
//            return null;
//        }
//    }
//
//    // =========================
//    // DTO Mapping
//    // =========================
//    @Override
//    public ProductDTO toDto(Product product) {
//
//        if (product == null) return null;
//
//        logger.debug("[toDto] Converting product -> id={}, title={}, price={}",
//                product.getId(),
//                product.getTitle(),
//                product.getPrice());
//
//        // ==========================================================
//        // IMAGE URL RESOLUTION - Robust Fallback Strategy
//        // Purpose: Ensure frontend always gets a valid image URL
//        // Priority: Entity imageUrl -> Entity getImage() -> Frontend placeholder
//        // ==========================================================
//        String resolvedImageUrl = product.getImageUrl(); // Primary: Direct field
//
//        if (resolvedImageUrl == null || resolvedImageUrl.trim().isEmpty()) {
//            logger.debug("[toDto] imageUrl field is null/empty, trying getImage() method");
//            resolvedImageUrl = product.getImageUrl(); // Fallback: Entity method
//
//            if (resolvedImageUrl != null && resolvedImageUrl.startsWith("http://localhost:3000")) {
//                logger.debug("[toDto] getImage() returned frontend placeholder, keeping as is");
//            }
//        }
//
//        // Final validation
//        if (resolvedImageUrl == null || resolvedImageUrl.trim().isEmpty()) {
//            logger.warn("[toDto] Product '{}' has no image in any field, frontend will use placeholder", product.getTitle());
//        } else {
//            logger.debug("[toDto] Image resolved successfully for product '{}': {}",
//                    product.getTitle(),
//                    resolvedImageUrl.length() > 50 ? resolvedImageUrl.substring(0, 50) + "..." : resolvedImageUrl);
//        }
//
//        ProductDTO dto = new ProductDTO(
//                product.getId(),
//                product.getTitle(),
//                product.getBrand(),
//                product.getModel(),
//                null,                 // lowestOfferPrice can be added later
//                resolvedImageUrl,       // ✅ Use resolved image URL
//                product.getPrice()     // ✅ include price
//        );
//
//        // ==========================================================
//        // MAP SPECIFICATIONS (Added for Compare Page)
//        // Purpose: Transfer all product specs to DTO for frontend display
//        // Includes: rating, storage, ram, battery, camera, display, processor
//        // ==========================================================
//
//        try {
//
//            // Map core specifications with null safety
//            dto.setRating(product.getRating());
//            dto.setStorage(product.getStorage());
//            dto.setRam(product.getRam());
//            dto.setBattery(product.getBattery());
//            dto.setCamera(product.getCamera());
//            dto.setDisplay(product.getDisplay());
//            dto.setProcessor(product.getProcessor());
//
//            logger.debug("[toDto] Additional specs -> camera={}, display={}, processor={}",
//                    product.getCamera(),
//                    product.getDisplay(),
//                    product.getProcessor());
//
//            logger.debug("[toDto] Specs mapped -> rating={}, storage={}, ram={}, battery={}",
//                    product.getRating(),
//                    product.getStorage(),
//                    product.getRam(),
//                    product.getBattery());
//
//        } catch (Exception e) {
//
//            logger.error("[toDto] Failed to map specifications for product {} : {}",
//                    product.getId(),
//                    e.getMessage());
//        }
//
//        // ==========================================================
//        // PRICE VALIDATION & DEBUGGING
//        // Purpose: Detect and log price issues for frontend troubleshooting
//        // Helps identify why frontend shows "RS 0" or "N/A"
//        // ==========================================================
//        if (product.getPrice() == null) {
//            logger.warn("[toDto] Product '{}' has NULL price. Frontend will display 'N/A' or 'RS 0'", product.getTitle());
//        } else {
//            logger.debug("[toDto] Product price validated: {} -> RS {}", product.getTitle(), product.getPrice());
//        }
//
//        // ==========================================================
//        // FINAL DTO VERIFICATION
//        // Purpose: Log complete DTO state for debugging
//        // Ensures all required fields are properly set before sending to frontend
//        // ==========================================================
//        logger.info("[toDto] Product {} converted to DTO successfully", product.getId());
//        logger.debug("[toDto] DTO Final State -> id={}, title={}, price={}, hasImage={}, imageLength={}",
//                dto.getId(),
//                dto.getTitle(),
//                dto.getPrice(),
//                dto.getImageUrl() != null && !dto.getImageUrl().contains("placeholder"),
//                dto.getImageUrl() != null ? dto.getImageUrl().length() : 0);
//
//        return dto;
//    }
//
//    @Override
//    public List<ProductDTO> toDtoList(List<Product> products) {
//
//        logger.debug("[toDtoList] Converting {} products to DTO list", products.size());
//
//        return products.stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }
//
//    // =========================
//    // Search & Filter
//    // =========================
//    @Override
//    public List<Product> findTop10ByTitleContainingIgnoreCase(String title) {
//
//        logger.debug("[search] Searching products by title fragment: {}", title);
//
//        return productRepository.findTop10ByTitleContainingIgnoreCase(title);
//    }
//
//    @Override
//    public List<ProductDTO> filterProducts(Double minPrice, Double maxPrice, List<String> brand,
//                                           Double minRating, List<String> storage, List<String> ram,
//                                           Integer battery) {
//
//        logger.info("[filterProducts] Applying filters: minPrice={}, maxPrice={}, brand={}, rating={}, storage={}, ram={}, battery={}",
//                minPrice, maxPrice, brand, minRating, storage, ram, battery);
//
//        Specification<Product> spec =
//                ProductSpecification.filterProducts(minPrice, maxPrice, brand, minRating, storage, ram, battery);
//
//        List<Product> filtered = productRepository.findAll(spec);
//
//        logger.debug("[filterProducts] Found {} matching products", filtered.size());
//
//        return toDtoList(filtered);
//    }
//
//    // =========================
//    // Featured Products
//    // =========================
//    @Override
//    public List<Product> getFeaturedProducts() {
//
//        logger.debug("[getFeaturedProducts] Fetching featured products");
//
//        return productRepository.findByFeatured(true);
//    }
//}

package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.ProductDTO;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
import com.example.SmartShop.AI.Assistant.Specification.ProductSpecification;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.SmartShop.AI.Assistant.Entity.ProductCategory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository, Cloudinary cloudinary) {
        this.productRepository = productRepository;
        this.cloudinary = cloudinary;
        logger.info("ProductServiceImpl initialized with Cloudinary integration");
        logger.debug("[INIT] ProductRepository instance -> {}", productRepository.getClass().getName());
    }

    // =========================
    // Product Creation
    // =========================
    @Override
    public Product createProduct(ProductDTO dto, MultipartFile image) {
        logger.info("[createProduct] Creating product: {}", dto.getTitle());
        logger.debug("[createProduct] Incoming DTO data -> id={}, title={}, brand={}, model={}, price={}, lowestOfferPrice={}",
                dto.getId(), dto.getTitle(), dto.getBrand(), dto.getModel(), dto.getPrice(), dto.getLowestOfferPrice());

        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setBrand(dto.getBrand());
        product.setModel(dto.getModel());
        product.setImageUrl(dto.getImageUrl());
        product.setPrice(dto.getPrice());


        // ================= CATEGORY =================
        if(dto.getCategory() != null){
            product.setCategory(dto.getCategory());
        }

// ================= LAPTOP =================
        product.setProcessor(dto.getProcessor()); // already exists in entity
        product.setGraphicsCard(dto.getGraphicsCard());
        product.setDisplaySize(dto.getDisplaySize());

// ================= HEADPHONE =================
        product.setHeadphoneType(dto.getHeadphoneType());
        product.setConnectivity(dto.getConnectivity());
        product.setBatteryLife(dto.getBatteryLife());
        product.setNoiseCancellation(dto.getNoiseCancellation());
        product.setDriverSize(dto.getDriverSize());


// ================= DEBUG LOG =================
        logger.debug("=== PRODUCT CREATE DEBUG ===");
        System.out.println("Category: " + dto.getCategory());
        System.out.println("Title: " + dto.getTitle());

        if (ProductCategory.LAPTOP.equals(dto.getCategory())) {
            System.out.println("Processor: " + dto.getProcessor());
            System.out.println("GPU: " + dto.getGraphicsCard());
        }

        if (ProductCategory.HEADPHONE.equals(dto.getCategory())) {
            System.out.println("Type: " + dto.getHeadphoneType());
            System.out.println("Noise Cancellation: " + dto.getNoiseCancellation());
        }

        if (dto.getPrice() == null) {
            logger.warn("[createProduct] WARNING: Product '{}' is being created with NULL price", dto.getTitle());
        } else {
            logger.debug("[createProduct] Product price set to {}", dto.getPrice());
        }

        Product saved = productRepository.save(product);
        logger.info("[createProduct] Product saved with ID: {}", saved.getId());
        logger.debug("[createProduct] Saved entity verification -> id={}, title={}, price={}", saved.getId(), saved.getTitle(), saved.getPrice());

        if (image != null && !image.isEmpty()) {
            logger.debug("[createProduct] Image detected -> name={}, size={} bytes", image.getOriginalFilename(), image.getSize());
            try {
                String imageUrl = uploadToCloudinary(image, saved.getId(), saved.getTitle());
                saved.setImageUrl(imageUrl);
                saved = productRepository.save(saved);
                logger.info("[createProduct] Image uploaded successfully: {}", imageUrl);
            } catch (IOException e) {
                logger.error("[createProduct] Cloudinary upload failed: {}", e.getMessage());
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        return saved;
    }

    @Override
    public List<Product> findTop10ByTitleContainingIgnoreCase(String title) {
        logger.debug("[search] Searching products by title fragment: {}", title);
        return productRepository.findTop10ByTitleContainingIgnoreCase(title);
    }


    // =========================
    // Safe Image Updates
    // =========================
    @Override
    public Product updateProductImage(UUID productId, MultipartFile imageFile) throws IOException {
        logger.info("[updateProductImage] Updating image for product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        logger.debug("[updateProductImage] Found product -> title={}, currentImage={}", product.getTitle(), product.getImageUrl());

        String imageUrl = uploadToCloudinary(imageFile, productId, product.getTitle());
        product.setImageUrl(imageUrl);
        product = productRepository.save(product);

        logger.info("[updateProductImage] Image updated successfully: {}", imageUrl);
        return product;
    }

    @Override
    public List<Product> updateMultipleProductImages(Map<UUID, MultipartFile> productImages) throws IOException {
        logger.info("[updateMultipleProductImages] Starting batch update for {} products", productImages.size());

        List<Product> updatedProducts = new ArrayList<>();
        List<String> uploadedUrls = new ArrayList<>();

        try {
            for (Map.Entry<UUID, MultipartFile> entry : productImages.entrySet()) {
                UUID productId = entry.getKey();
                MultipartFile file = entry.getValue();

                logger.debug("[updateMultipleProductImages] Processing product {}", productId);

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                String imageUrl = uploadToCloudinary(file, productId, product.getTitle());
                uploadedUrls.add(imageUrl);

                product.setImageUrl(imageUrl);
                updatedProducts.add(productRepository.save(product));

                logger.info("[updateMultipleProductImages] Updated product {}", productId);
            }

            return updatedProducts;
        } catch (Exception e) {
            logger.error("[updateMultipleProductImages] Batch update failed: {}", e.getMessage());
            cleanupFailedUploads(uploadedUrls);
            throw new IOException("Batch image update failed", e);
        }
    }

    private String uploadToCloudinary(MultipartFile image, UUID productId, String productTitle) throws IOException {
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        String uniqueName = "product_" + productId + "_" + UUID.randomUUID() + "." + extension;

        logger.debug("[Cloudinary] Upload starting -> file={}, generatedName={}", image.getOriginalFilename(), uniqueName);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(
                image.getBytes(),
                ObjectUtils.asMap(
                        "public_id", uniqueName,
                        "folder", "products",
                        "overwrite", true,
                        "context", "title=" + productTitle
                )
        );

        String secureUrl = (String) result.get("secure_url");
        logger.debug("[Cloudinary] Upload result -> {}", secureUrl);
        return secureUrl;
    }

    private void cleanupFailedUploads(List<String> urls) {
        logger.warn("[cleanupFailedUploads] Cleaning {} uploaded files after failure", urls.size());
        for (String url : urls) {
            try {
                String publicId = extractPublicId(url);
                if (publicId != null)
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                logger.warn("Failed cleanup for URL {}: {}", url, e.getMessage());
            }
        }
    }

    private String extractPublicId(String url) {
        try {
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            return lastPart.substring(0, lastPart.lastIndexOf('.'));
        } catch (Exception e) {
            logger.warn("Failed to extract publicId from URL {}", url);
            return null;
        }
    }

    // =========================
    // DTO Mapping
    // =========================
    @Override
    public ProductDTO toDto(Product product) {
        if (product == null) return null;

        logger.debug("[toDto] Converting product -> id={}, title={}, price={}", product.getId(), product.getTitle(), product.getPrice());

        String resolvedImageUrl = product.getImageUrl();
        if (resolvedImageUrl == null || resolvedImageUrl.trim().isEmpty()) {
            logger.warn("[toDto] Product '{}' has no image, frontend will use placeholder", product.getTitle());
        }

        // ================================
        //  Compute lowestOfferPrice safely
        // ================================
        Double lowestOfferPrice = null;
        try {
            if (product.getOffers() != null && !product.getOffers().isEmpty()) {
                lowestOfferPrice = product.getOffers().stream()
                        .filter(o -> Boolean.TRUE.equals(o.getAvailable()) && o.getPrice() != null)
                        .map(o -> o.getPrice())
                        .min(Double::compareTo)
                        .orElse(null);
                logger.debug("[toDto] Computed lowestOfferPrice = {}", lowestOfferPrice);
            }
        } catch (Exception e) {
            logger.error("[toDto] Error computing lowestOfferPrice: {}", e.getMessage());
        }

        // ================================
        //  Resolve final price
        // ================================
        Double finalPrice = product.getPrice();
        if (finalPrice == null && lowestOfferPrice != null) {
            logger.info("[toDto] Using lowestOfferPrice as fallback for '{}'", product.getTitle());
            finalPrice = lowestOfferPrice;
        }
        if (finalPrice == null) {
            logger.warn("[toDto] No price available for '{}'", product.getTitle());
        }

        // ================================
        //  Create DTO
        // ================================
        ProductDTO dto = new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getBrand(),
                product.getModel(),
                lowestOfferPrice,   // FIXED: now includes lowest offer price
                resolvedImageUrl,
                finalPrice          // FIXED: uses fallback if needed
        );

        try {
            dto.setRating(product.getRating());
            dto.setStorage(product.getStorage());
            dto.setRam(product.getRam());
            dto.setBattery(product.getBattery());
            dto.setCamera(product.getCamera());
            dto.setDisplay(product.getDisplay());
            dto.setProcessor(product.getProcessor());

            //  NEW FIELDS
            dto.setCategory(product.getCategory());

            dto.setGraphicsCard(product.getGraphicsCard());
            dto.setDisplaySize(product.getDisplaySize());

            dto.setHeadphoneType(product.getHeadphoneType());
            dto.setConnectivity(product.getConnectivity());
            dto.setBatteryLife(product.getBatteryLife());
            dto.setNoiseCancellation(product.getNoiseCancellation());
            dto.setDriverSize(product.getDriverSize());

        } catch (Exception e) {
            logger.error("[toDto] Failed to map specifications for product {} : {}", product.getId(), e.getMessage());
        }

        logger.info("[toDto] Product {} converted to DTO successfully", product.getId());
        return dto;
    }

    @Override
    public List<ProductDTO> toDtoList(List<Product> products) {
        logger.debug("[toDtoList] Converting {} products to DTO list", products.size());
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> filterProducts(
            Double minPrice,
            Double maxPrice,
            List<String> brand,
            Double minRating,
            List<String> storage,
            List<String> ram,
            Integer battery,
            ProductCategory category
    ) {

        logger.info("[filterProducts] Applying filters -> minPrice={}, maxPrice={}, brand={}, rating={}, storage={}, ram={}, battery={}, category={}",
                minPrice, maxPrice, brand, minRating, storage, ram, battery, category);

        try {
            Specification<Product> spec =
                    ProductSpecification.filterProducts(
                            minPrice,
                            maxPrice,
                            brand,
                            minRating,
                            storage,
                            ram,
                            battery,
                            category
                    );

            List<Product> filtered = productRepository.findAll(spec);

            if (filtered == null || filtered.isEmpty()) {
                logger.warn("[filterProducts] No products found for given filters");
                return Collections.emptyList();
            }

            logger.debug("[filterProducts] Found {} matching products", filtered.size());

            return toDtoList(filtered);

        } catch (Exception e) {
            logger.error("[filterProducts] Error while filtering products: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    // =========================
    // Featured Products
    // =========================
    @Override
    public List<Product> getFeaturedProducts() {
        logger.debug("[getFeaturedProducts] Fetching featured products");
        return productRepository.findByFeatured(true);
    }
}