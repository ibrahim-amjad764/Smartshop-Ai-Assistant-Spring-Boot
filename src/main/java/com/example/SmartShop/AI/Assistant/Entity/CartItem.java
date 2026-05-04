package com.example.SmartShop.AI.Assistant.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

/**
 * Represents a product added to a user's cart.
 *
 * Business Rules:
 * - One user can have multiple cart items.
 * - Only one cart item per product per user (unique constraint).
 * - Quantity must always be >= 1.
 */
@Entity
@Table(name = "cart_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "product_id" })
})
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    /**
     * Many cart items belong to one user.
     * LAZY loading improves performance.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private com.example.SmartShop.AI.Assistant.Entity.User user;

    /**
     * Many cart items reference one product.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity must always be positive.
     */
    @Column(nullable = false)
    private int quantity = 1;

    /**
     * Safe quantity update with validation.
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        this.quantity = newQuantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", productId=" + (product != null ? product.getId() : null) +
                ", quantity=" + quantity +
                '}';
    }
}