package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.OfferResponseDTO;
import com.example.SmartShop.AI.Assistant.Dto.StoreDetailDTO;
import com.example.SmartShop.AI.Assistant.Dto.StoreRatingDTO;
import com.example.SmartShop.AI.Assistant.Dto.StoreSummaryDTO;
import com.example.SmartShop.AI.Assistant.Entity.Offer;
import com.example.SmartShop.AI.Assistant.Entity.Store;
import com.example.SmartShop.AI.Assistant.Repository.OfferRepository;
import com.example.SmartShop.AI.Assistant.Repository.StoreRatingRepository;
import com.example.SmartShop.AI.Assistant.Repository.StoreRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    private final StoreRepository storeRepository;
    private final OfferRepository offerRepository;
    private final StoreRatingRepository storeRatingRepository;

    public StoreService(
            StoreRepository storeRepository,
            OfferRepository offerRepository,
            StoreRatingRepository storeRatingRepository
    ) {
        this.storeRepository = storeRepository;
        this.offerRepository = offerRepository;
        this.storeRatingRepository = storeRatingRepository;
    }

    // Get all stores
    public List<Store> getAllStores() {
        log.info("Fetching all stores");
        return storeRepository.findAll();
    }

    public Page<StoreSummaryDTO> getAllStores(Pageable pageable) {
        System.out.println("[StoreService] Fetching stores from repository...");
        Page<Store> storesPage = storeRepository.findAll(pageable);
        System.out.println("[StoreService] Found " + storesPage.getTotalElements() + " total stores");
        System.out.println("[StoreService] Page content size: " + storesPage.getContent().size());
        return storesPage.map(this::toSummaryDto);
    }

    public StoreDetailDTO getStoreDetails(UUID storeId) {
        Store store = findStoreEntityById(storeId);
        return new StoreDetailDTO(
                store.getId(),
                store.getName(),
                store.getLogoUrl(),
                store.getApiUrl(),
                getStoreRating(storeId)
        );
    }

    public Store findStoreEntityById(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found with id " + storeId));
    }

    public Page<OfferResponseDTO> getStoreOffers(UUID storeId, Pageable pageable) {
        var offersPage = offerRepository.findByStore_IdAndAvailableTrue(storeId, pageable);
        Double cheapestPrice = offersPage.getContent().stream()
                .map(Offer::getPrice)
                .min(Double::compareTo)
                .orElse(null);

        return offersPage.map(offer -> toOfferResponse(offer, cheapestPrice));
    }

    public StoreRatingDTO getStoreRating(UUID storeId) {
        Double avg = storeRatingRepository.averageByStoreId(storeId);
        long count = storeRatingRepository.countByStore_Id(storeId);
        return new StoreRatingDTO(avg != null ? avg : 0.0, count);
    }

    // Search stores by name (case-insensitive)
    public List<Store> findByNameIgnoreCase(String query) {
        log.info("Searching stores with query: {}", query);
        return storeRepository.findByNameContainingIgnoreCase(query);
    }

    private StoreSummaryDTO toSummaryDto(Store store) {
        return new StoreSummaryDTO(store.getId(), store.getName(), store.getLogoUrl());
    }

    private OfferResponseDTO toOfferResponse(Offer offer, Double cheapestPrice) {
        boolean isCheapest = cheapestPrice != null
                && offer.getPrice() != null
                && Double.compare(offer.getPrice(), cheapestPrice) == 0;
        return new OfferResponseDTO(
                offer.getId(),
                offer.getStore().getId(),
                offer.getStore().getName(),
                offer.getStore().getLogoUrl(),
                offer.getPrice(),
                offer.getAvailable(),
                offer.getUrl(),
                isCheapest
        );
    }
}