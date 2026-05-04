package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.PriceHistoryDTO;
import com.example.SmartShop.AI.Assistant.Dto.ProductDTO;
import com.example.SmartShop.AI.Assistant.Dto.OfferResponseDTO;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Entity.ProductCategory;
import com.example.SmartShop.AI.Assistant.Repository.PriceHistoryRepository;
import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
import com.example.SmartShop.AI.Assistant.Service.OfferService;
import com.example.SmartShop.AI.Assistant.Service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final PriceHistoryRepository priceHistoryRepository;
    private final OfferService offerService;

    public ProductController(ProductRepository productRepository,
                             ProductService productService,
                             PriceHistoryRepository priceHistoryRepository,
                             OfferService offerService) {

        this.productRepository = productRepository;
        this.productService = productService;
        this.priceHistoryRepository = priceHistoryRepository;
        this.offerService = offerService;

        System.out.println(" ProductController initialized successfully");
        System.out.println(" ProductRepository injected: " + (productRepository != null));
        System.out.println(" PriceHistoryRepository injected: " + (priceHistoryRepository != null));
    }

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        logger.info("Fetching all products");
        return productService.toDtoList(productRepository.findAll());
    }

    @GetMapping("/search")
    public List<ProductDTO> searchProducts(@RequestParam String query) {
        logger.info("Search API called with query: {}", query);
        return productService.toDtoList(productRepository.findByTitleContainingIgnoreCase(query));
    }

    @GetMapping("/filter")
    public List<ProductDTO> filterProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) List<String> storage,
            @RequestParam(required = false) List<String> ram,
            @RequestParam(required = false) Integer battery,
            @RequestParam(required = false) ProductCategory category   //  FIX ADDED
    ) {
        logger.info("========== FILTER API HIT ==========");

        return productService.filterProducts(
                minPrice,
                maxPrice,
                brand,
                minRating,
                storage,
                ram,
                battery,
                category   //  FIX ADDED
        );
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable UUID id) {
        logger.info("Fetching product by ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with id {}", id);
                    return new RuntimeException("Product not found with id " + id);
                });

        return productService.toDto(product);
    }

    @GetMapping("/{id}/offers")
    public List<OfferResponseDTO> getProductOffers(@PathVariable UUID id) {
        logger.info("Fetching offers for product ID: {}", id);
        return offerService.getOfferResponsesForProduct(id);
    }

    @PostMapping("/create")
    public Product createProduct(
            @RequestPart("product") ProductDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        logger.info("Received product creation request for title: {}", dto.getTitle());
        Product product = productService.createProduct(dto, image);
        logger.info("Product created successfully with ID: {}", product.getId());
        return product;
    }

    @GetMapping("/{id}/price-history")
    public List<PriceHistoryDTO> getPriceHistory(@PathVariable UUID id) {
        logger.info("Fetching price history for product ID: {}", id);
        try {
            List<PriceHistoryDTO> history = priceHistoryRepository
                    .findByOffer_Product_IdOrderByCheckedAtAsc(id)
                    .stream()
                    .filter(ph -> ph.getOffer() != null)
                    .map(ph -> new PriceHistoryDTO(
                            ph.getOffer().getId(),
                            ph.getPrice(),
                            ph.getCheckedAt()
                    ))
                    .toList();

            logger.info("Price history records found: {}", history.size());
            return history;

        } catch (Exception e) {
            logger.error("Error fetching price history: ", e);
            return Collections.emptyList();
        }
    }
}