package com.example.SmartShop.AI.Assistant.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "search_query", indexes = {
        @Index(name = "idx_search_query_text", columnList = "query_text"),
        @Index(name = "idx_search_query_created", columnList = "created_at"),
        @Index(name = "idx_search_query_user", columnList = "user_id")
})
public class SearchQuery {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "query_text", nullable = false, length = 500)
    private String queryText;

    @Column(name = "user_budget")
    private Double userBudget;

    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { this.queryText = queryText; }
    public Double getUserBudget() { return userBudget; }
    public void setUserBudget(Double userBudget) { this.userBudget = userBudget; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
