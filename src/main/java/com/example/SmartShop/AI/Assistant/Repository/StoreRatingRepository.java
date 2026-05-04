package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.StoreRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface StoreRatingRepository extends JpaRepository<StoreRating, UUID> {

    long countByStore_Id(UUID storeId);

    @Query("SELECT COALESCE(AVG(sr.rating), 0) FROM StoreRating sr WHERE sr.store.id = :storeId")
    Double averageByStoreId(@Param("storeId") UUID storeId);
}
