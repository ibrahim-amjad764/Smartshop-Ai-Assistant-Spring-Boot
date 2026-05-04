package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.FavoriteProductDTO;
import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
import com.example.SmartShop.AI.Assistant.Entity.Favorite;
import com.example.SmartShop.AI.Assistant.Entity.Offer;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Entity.User;
import com.example.SmartShop.AI.Assistant.Repository.FavoriteRepository;
import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
import com.example.SmartShop.AI.Assistant.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Favorites.
 * Handles business logic, DTO mapping, and safe duplicate handling.
 * Computes effective price from offers to match ProductDTO behavior.
 */
@Service
@Transactional(readOnly = true)
public class FavoriteService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET ALL FAVORITES BY USER EMAIL
    // =========================================================
    public List<FavoriteProductDTO> getFavoritesByEmail(String email) {
        log.info("[FavoriteService] Fetching favorites for user={}", email);

        User user = getUserByEmail(email);

        return favoriteRepository.findAllByUser(user)
                .stream()
                .map(fav -> mapToDTO(fav.getProduct()))
                .toList();
    }

    // =========================================================
    // ADD FAVORITE BY EMAIL + PRODUCT ID
    // =========================================================
    @Transactional
    public void addFavoriteByEmail(String email, UUID productId) {
        log.info("[FavoriteService] Adding favorite | user={} productId={}", email, productId);

        User user = getUserByEmail(email);
        Product product = getProductById(productId);

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            log.info("[FavoriteService] Favorite already exists | user={} productId={}", email, productId);
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);

        log.info("[FavoriteService] Favorite added successfully | user={} productId={}", email, productId);
    }

    // =========================================================
    // REMOVE FAVORITE
    // =========================================================
    @Transactional
    public boolean removeFavoriteByEmail(String email, UUID productId) {
        log.info("[FavoriteService] Removing favorite | user={} productId={}", email, productId);

        User user = getUserByEmail(email);

        int deletedRows = favoriteRepository.deleteAllByUserAndProductId(user, productId);

        log.info("[FavoriteService] Favorites deleted count = {}", deletedRows);

        return deletedRows > 0;
    }

    // =========================================================
    // CHECK IF FAVORITE EXISTS
    // =========================================================
    public boolean isFavoriteByEmail(String email, UUID productId) {
        log.info("[FavoriteService] Checking favorite | user={} productId={}", email, productId);

        User user = getUserByEmail(email);
        Product product = getProductById(productId);

        return favoriteRepository.existsByUserAndProduct(user, product);
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    /**
     * Fetch user by email with null check
     */
    private User getUserByEmail(String email) {
        log.debug("[FavoriteService] Fetching user by email={}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with email: " + email));
    }

    /**
     * Fetch product by UUID with null check
     */
    private Product getProductById(UUID productId) {
        log.debug("[FavoriteService] Fetching product by id={}", productId);

        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found with id: " + productId));
    }

    /**
     * Compute effective price from offers (matches ProductDTO logic)
     * Priority: lowestOfferPrice > product.price > null
     */
    private Double computeEffectivePrice(Product product, List<Offer> offers) {
        // First try lowest offer price from available offers
        Double lowest = computeLowestOfferPrice(offers);
        if (lowest != null) {
            log.debug("[FavoriteService]  Using lowestOfferPrice: {}", lowest);
            return lowest;
        }

        // Fallback to product main price
        if (product.getPrice() != null) {
            log.debug("[FavoriteService]  Using product.price: {}", product.getPrice());
            return product.getPrice();
        }

        log.warn("[FavoriteService]  No price available for product: {}", product.getId());
        return null;
    }

    /**
     * Compute lowest offer price from offers list
     * Only considers available offers with non-null prices
     */
    private Double computeLowestOfferPrice(List<Offer> offers) {
        if (offers == null || offers.isEmpty()) {
            return null;
        }

        return offers.stream()
                .filter(o -> o.getPrice() != null && o.getAvailable() != null && o.getAvailable())
                .map(Offer::getPrice)
                .min(Double::compareTo)
                .orElse(null);
    };

    /**
     * Convert Product → FavoriteProductDTO
     * Handles nested OfferDTO mapping and computes effective price
     */
    private FavoriteProductDTO mapToDTO(Product product) {
        log.debug("[FavoriteService] Mapping product to FavoriteProductDTO | productId={}", product.getId());

        // Handle lazy loading - ensure offers are loaded
        List<Offer> productOffers = product.getOffers();
        if (productOffers == null) {
            productOffers = List.of();
            log.debug("[FavoriteService] No offers found for product: {}", product.getId());
        } else {
            log.debug("[FavoriteService] Found {} offers for product: {}", productOffers.size(), product.getId());
        }

        // Compute effective price from offers (matches ProductDTO behavior)
        Double effectivePrice = computeEffectivePrice(product, productOffers);
        Double lowestOfferPrice = computeLowestOfferPrice(productOffers);

        // Map offers to DTOs
        List<OfferDTO> offers = productOffers.stream()
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
                }).toList();

        // Resolve image safely
        String resolvedImage = product.getImageUrl();
        if (resolvedImage == null || resolvedImage.isBlank()) {
            log.warn("[FavoriteService]  Product image missing | productId={}", product.getId());
        } else {
            log.debug("[FavoriteService]  Resolved product image | productId={}", product.getId());
        }

        log.debug("[FavoriteService]  Computed prices | productId={} effectivePrice={} lowestOfferPrice={}",
                product.getId(), effectivePrice, lowestOfferPrice);

        return new FavoriteProductDTO(
                product.getId(),
                product.getTitle(),
                product.getBrand(),
                product.getModel(),
                resolvedImage,
                effectivePrice,      //  Computed effective price from offers
                lowestOfferPrice,    //  Lowest offer price for frontend
                offers
        );
    }
}