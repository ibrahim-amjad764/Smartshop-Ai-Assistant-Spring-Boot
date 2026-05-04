package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.Store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    // Case-insensitive search by name
    List<Store> findByNameIgnoreCase(String name);
    List<Store> findByNameContainingIgnoreCase(String name);

}