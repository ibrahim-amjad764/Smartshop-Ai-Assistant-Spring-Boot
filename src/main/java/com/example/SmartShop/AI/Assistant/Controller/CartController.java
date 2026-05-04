package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.CartItemDTO;
import com.example.SmartShop.AI.Assistant.Dto.QuantityRequestDTO;
import com.example.SmartShop.AI.Assistant.Service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(Authentication auth) {
        String email = extractEmail(auth);
        List<CartItemDTO> cart = cartService.getCartByEmail(email);
        return ResponseEntity.ok(cart.isEmpty() ? Collections.emptyList() : cart);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> addToCart(Authentication auth,
                                          @PathVariable UUID productId,
                                          @RequestParam(defaultValue = "1") int quantity) {

        String email = extractEmail(auth);
        cartService.addToCart(email, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping
    public ResponseEntity<Void> addToCart(Authentication auth,
                                          @Valid @RequestBody AddToCartRequest req) {

        String email = extractEmail(auth);
        cartService.addToCart(email, req.getProductId(), req.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateQuantity(Authentication auth,
                                               @PathVariable UUID productId,
                                               @Valid @RequestBody QuantityRequestDTO request) {
        String email = extractEmail(auth);
        cartService.updateQuantity(email, productId, request.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromCart(Authentication auth,
                                               @PathVariable UUID productId) {
        String email = extractEmail(auth);
        boolean removed = cartService.removeFromCart(email, productId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication auth) {
        String email = extractEmail(auth);
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }

    private String extractEmail(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return auth.getName();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToCartRequest {
        @NotNull(message = "productId is required")
        private UUID productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity = 1;
    }
}