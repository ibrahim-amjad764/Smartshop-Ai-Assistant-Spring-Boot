package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
import com.example.SmartShop.AI.Assistant.Dto.OfferResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

public interface OfferService {

    Page<OfferDTO> getAllOffers(Pageable pageable);

    Page<OfferDTO> getOffersByProduct(UUID productId, Pageable pageable);

    Optional<OfferDTO> getCheapestOffer(UUID productId);

    Page<OfferDTO> searchOffers(String query, Double budget, Pageable pageable);
    List<OfferResponseDTO> getOfferResponsesForProduct(UUID productId);

    /**
     *  SMART PRICE COMPARISON ENGINE
     */
    Map<String, Object> comparePrices(UUID productId);
}