//package com.example.SmartShop.AI.Assistant.Service;
//
//import com.example.SmartShop.AI.Assistant.Dto.CartItemDTO;
//import com.example.SmartShop.AI.Assistant.Entity.CartItem;
//import com.example.SmartShop.AI.Assistant.Entity.Product;
//import com.example.SmartShop.AI.Assistant.Entity.User;
//import com.example.SmartShop.AI.Assistant.Repository.CartRepository;
//import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
//import com.example.SmartShop.AI.Assistant.Repository.UserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@Transactional
//@Slf4j
//@RequiredArgsConstructor
//public class CartService {
//
//    private final CartRepository cartRepository;
//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
//
//    // ======================================================
//    // GET CART ITEMS
//    // ======================================================
//    @Transactional(readOnly = true)
//    public List<CartItemDTO> getCartByEmail(String email) {
//
//        log.info("📦 [CartService] Fetching cart for user={}", email);
//
//        User user = getUserByEmail(email);
//
//        List<CartItem> items = cartRepository.findAllByUser(user);
//
//        log.info("📦 [CartService] Found {} cart items for user={}", items.size(), email);
//
//        return items.stream()
//                .map(this::mapToDTO)
//                .toList(); // modern Java
//    }
//
//    // ======================================================
//    // ADD TO CART
//    // ======================================================
//    public void addToCart(String email, Long productId, int quantity) {
//
//        log.info("🛒 [CartService] Add request | email={} productId={} quantity={}",
//                email, productId, quantity);
//
//        validateQuantity(quantity);
//
//        User user = getUserByEmail(email);
//        Product product = getProductById(productId);
//
//        cartRepository.findByUserAndProductId(user, productId)
//                .ifPresentOrElse(existingItem -> {
//
//                    int updatedQuantity = existingItem.getQuantity() + quantity;
//                    existingItem.setQuantity(updatedQuantity);
//
//                    // Hibernate dirty checking will auto-update
//                    log.info("🔁 [CartService] Updated existing item productId={} newQuantity={}",
//                            productId, updatedQuantity);
//
//                }, () -> {
//
//                    CartItem newItem = createNewCartItem(user, product, quantity);
//                    cartRepository.save(newItem);
//
//                    log.info("🆕 [CartService] Created new cart item productId={}",
//                            productId);
//                });
//
//        log.info("✅ [CartService] Add to cart completed for user={}", email);
//    }
//
//    // ======================================================
//    // UPDATE QUANTITY
//    // ======================================================
//    public void updateQuantity(String email, Long productId, int quantity) {
//
//        log.info("✏️ [CartService] Update request | email={} productId={} quantity={}",
//                email, productId, quantity);
//
//        validateQuantity(quantity);
//
//        User user = getUserByEmail(email);
//
//        CartItem cartItem = cartRepository.findByUserAndProductId(user, productId)
//                .orElseThrow(() -> {
//                    log.error("❌ [CartService] Product not found in cart productId={} user={}",
//                            productId, email);
//                    return new EntityNotFoundException("Product not in cart: " + productId);
//                });
//
//        cartItem.setQuantity(quantity); // dirty checking handles update
//
//        log.info("✅ [CartService] Quantity updated productId={} newQuantity={}",
//                productId, quantity);
//    }
//
//    // ======================================================
//    // REMOVE FROM CART
//    // ======================================================
//    public boolean removeFromCart(String email, Long productId) {
//
//        log.info("🗑 [CartService] Remove request | email={} productId={}",
//                email, productId);
//
//        User user = getUserByEmail(email);
//
//        return cartRepository.findByUserAndProductId(user, productId)
//                .map(item -> {
//                    cartRepository.delete(item);
//                    log.info("✅ [CartService] Removed productId={} from cart", productId);
//                    return true;
//                })
//                .orElseGet(() -> {
//                    log.warn("⚠️ [CartService] Product not found in cart productId={} user={}",
//                            productId, email);
//                    return false;
//                });
//    }
//
//    // ======================================================
//    // CLEAR CART
//    // ======================================================
//    public void clearCart(String email) {
//
//        log.info("🧹 [CartService] Clear cart request | user={}", email);
//
//        User user = getUserByEmail(email);
//
//        // Bulk delete (no pre-fetch)
//        cartRepository.deleteAllByUser(user);
//
//        log.info("✅ [CartService] Cart cleared successfully for user={}", email);
//    }
//
//    // ======================================================
//    // HELPER METHODS
//    // ======================================================
//
//    private User getUserByEmail(String email) {
//
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> {
//                    log.error("❌ [CartService] User not found email={}", email);
//                    return new EntityNotFoundException("User not found: " + email);
//                });
//    }
//
//    private Product getProductById(Long productId) {
//
//        return productRepository.findById(productId)
//                .orElseThrow(() -> {
//                    log.error("❌ [CartService] Product not found id={}", productId);
//                    return new EntityNotFoundException("Product not found: " + productId);
//                });
//    }
//
//    private void validateQuantity(int quantity) {
//
//        if (quantity < 1) {
//            log.error("❌ [CartService] Invalid quantity={}", quantity);
//            throw new IllegalArgumentException("Quantity must be at least 1");
//        }
//    }
//
//    /**
//     * Safe DTO mapping.
//     * Prevents NullPointerException if offers are missing.
//     */
//    private CartItemDTO mapToDTO(CartItem item) {
//
//        double price = 0.0;
//
//        if (item.getProduct().getOffers() != null &&
//                !item.getProduct().getOffers().isEmpty()) {
//
//            price = item.getProduct().getOffers()
//                    .stream()
//                    .findFirst()
//                    .map(offer -> offer.getPrice())
//                    .orElse(0.0);
//        }
//
//        return new CartItemDTO(
//                item.getProduct().getId(),
//                item.getProduct().getTitle(),
//                item.getProduct().getBrand(),
//                item.getProduct().getModel(),
//                item.getQuantity(),
//                price
//        );
//    }
//
//    private CartItem createNewCartItem(User user, Product product, int quantity) {
//
//        CartItem item = new CartItem();
//        item.setUser(user);
//        item.setProduct(product);
//        item.setQuantity(quantity);
//
//        return item;
//    }
//}

package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.CartItemDTO;
import com.example.SmartShop.AI.Assistant.Entity.CartItem;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Entity.User;
import com.example.SmartShop.AI.Assistant.Repository.CartRepository;
import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
import com.example.SmartShop.AI.Assistant.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ======================================================
    // GET CART ITEMS
    // ======================================================
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartByEmail(String email) {

        log.info(" [CartService] Fetching cart for user={}", email);

        User user = getUserByEmail(email);

        List<CartItem> items = cartRepository.findAllByUser(user);

        log.info(" [CartService] Found {} cart items for user={}", items.size(), email);

        return items.stream()
                .map(this::mapToDTO)
                .toList(); // modern Java
    }

    // ======================================================
    // ADD TO CART
    // ======================================================
    public void addToCart(String email, UUID productId, int quantity) {

        log.info(" [CartService] Add request | email={} productId={} quantity={}",
                email, productId, quantity);

        validateQuantity(quantity);

        User user = getUserByEmail(email);
        Product product = getProductById(productId);

        cartRepository.findByUserAndProductId(user, productId)
                .ifPresentOrElse(existingItem -> {

                    int updatedQuantity = existingItem.getQuantity() + quantity;
                    existingItem.setQuantity(updatedQuantity);

                    log.info(" [CartService] Updated existing item productId={} newQuantity={}",
                            productId, updatedQuantity);

                }, () -> {

                    CartItem newItem = createNewCartItem(user, product, quantity);
                    cartRepository.save(newItem);

                    log.info(" [CartService] Created new cart item productId={}", productId);
                });

        log.info(" [CartService] Add to cart completed for user={}", email);
    }

    private CartItem createNewCartItem(User user, Product product, int quantity) {

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(quantity);

        return item;
    }

    // ======================================================
    // UPDATE QUANTITY
    // ======================================================
    public void updateQuantity(String email, UUID productId, int quantity) {

        log.info(" [CartService] Update request | email={} productId={} quantity={}",
                email, productId, quantity);

        validateQuantity(quantity);

        User user = getUserByEmail(email);

        CartItem cartItem = cartRepository.findByUserAndProductId(user, productId)
                .orElseThrow(() -> {
                    log.error(" [CartService] Product not found in cart productId={} user={}",
                            productId, email);
                    return new EntityNotFoundException("Product not in cart: " + productId);
                });

        cartItem.setQuantity(quantity);

        log.info(" [CartService] Quantity updated productId={} newQuantity={}", productId, quantity);
    }

    // ======================================================
    // REMOVE FROM CART
    // ======================================================
    public boolean removeFromCart(String email, UUID productId) {

        log.info("🗑 [CartService] Remove request | email={} productId={}", email, productId);

        User user = getUserByEmail(email);

        return cartRepository.findByUserAndProductId(user, productId)
                .map(item -> {
                    cartRepository.delete(item);
                    log.info(" [CartService] Removed productId={} from cart", productId);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn(" [CartService] Product not found in cart productId={} user={}",
                            productId, email);
                    return false;
                });
    }

    // ======================================================
    // CLEAR CART
    // ======================================================
    public void clearCart(String email) {

        log.info("🧹 [CartService] Clear cart request | user={}", email);

        User user = getUserByEmail(email);

        cartRepository.deleteAllByUser(user);

        log.info(" [CartService] Cart cleared successfully for user={}", email);
    }

    // ======================================================
    // HELPER METHODS
    // ======================================================

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error(" [CartService] User not found email={}", email);
                    return new EntityNotFoundException("User not found: " + email);
                });
    }

    private Product getProductById(UUID productId) {

        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error(" [CartService] Product not found id={}", productId);
                    return new EntityNotFoundException("Product not found: " + productId);
                });
    }

    private void validateQuantity(int quantity) {

        if (quantity < 1) {
            log.error(" [CartService] Invalid quantity={}", quantity);
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
    }

    private CartItemDTO mapToDTO(CartItem item) {

        System.out.println(" [CartService] mapToDTO called for CartItem");

        double price = 0.0;

        // ======================================================
        // PRICE RESOLUTION (Offer → Product fallback)
        // ======================================================
        if (item.getProduct().getOffers() != null &&
                !item.getProduct().getOffers().isEmpty()) {

            price = item.getProduct().getOffers()
                    .stream()
                    .findFirst()
                    .map(offer -> offer.getPrice())
                    .orElse(0.0);

            System.out.println(" [CartService] Price resolved from OFFER: " + price);

        } else if (item.getProduct().getPrice() != null) {

            price = item.getProduct().getPrice();

            System.out.println(" [CartService] Price resolved from PRODUCT.price: " + price);

        } else {

            System.out.println(" [CartService] No price found for product: "
                    + item.getProduct().getId());
        }

        // ======================================================
        // IMAGE RESOLUTION
        // ======================================================
        String image = item.getProduct().getImage();

        System.out.println(" [CartService] Product image resolved: " + image);

        // ======================================================
        // PRODUCT DEBUG INFO
        // ======================================================
        System.out.println(" [CartService] Product Debug Info:");
        System.out.println("   Product ID: " + item.getProduct().getId());
        System.out.println("   Title: " + item.getProduct().getTitle());
        System.out.println("   Brand: " + item.getProduct().getBrand());
        System.out.println("   Model: " + item.getProduct().getModel());
        System.out.println("   Quantity: " + item.getQuantity());
        System.out.println("   Final Price: " + price);

        // ======================================================
        // CREATE DTO (CORRECT ORDER)
        // ======================================================
        CartItemDTO dto = new CartItemDTO(
                item.getProduct().getId(),
                item.getProduct().getTitle(),
                item.getProduct().getBrand(),
                item.getProduct().getModel(),
                image,
                item.getQuantity(),
                price
        );

        System.out.println(" [CartService] CartItemDTO created:");
        System.out.println("   productId=" + dto.getProductId());
        System.out.println("   title=" + dto.getTitle());
        System.out.println("   image=" + dto.getImage());
        System.out.println("   quantity=" + dto.getQuantity());
        System.out.println("   price=" + dto.getPrice());

        return dto;
    }
}