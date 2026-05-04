package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
import com.example.SmartShop.AI.Assistant.Dto.OfferResponseDTO;
import com.example.SmartShop.AI.Assistant.Entity.Offer;
import com.example.SmartShop.AI.Assistant.Repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private static final Logger log = LoggerFactory.getLogger(OfferServiceImpl.class);

    private final OfferRepository offerRepository;

    @Override
    public Page<OfferDTO> getAllOffers(Pageable pageable) {
        log.info("[OfferService] Fetching all offers | page={} size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return offerRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public Page<OfferDTO> getOffersByProduct(UUID productId, Pageable pageable) {
        log.info("[OfferService] Fetching offers for productId={}", productId);

        return offerRepository.findByProduct_IdAndAvailableTrue(productId, pageable)
                .map(this::toDto);
    }

    @Override
    public Optional<OfferDTO> getCheapestOffer(UUID productId) {
        log.info("[OfferService] Fetching cheapest offer for productId={}", productId);

        return offerRepository
                .findFirstByProduct_IdAndAvailableTrueOrderByPriceAsc(productId)
                .map(this::toDto);
    }

    @Override
    public Page<OfferDTO> searchOffers(String query, Double budget, Pageable pageable) {
        log.info("[OfferService] Searching offers | query='{}' budget={}", query, budget);

        return offerRepository.search(query, budget, pageable)
                .map(this::toDto);
    }

    @Override
    public List<OfferResponseDTO> getOfferResponsesForProduct(UUID productId) {
        List<Offer> offers = offerRepository.findByProduct_IdAndAvailableTrue(productId);
        if (offers == null || offers.isEmpty()) {
            return List.of();
        }

        List<Offer> sortedOffers = offers.stream()
                .sorted(Comparator.comparingDouble(Offer::getPrice))
                .toList();
        Double cheapest = sortedOffers.get(0).getPrice();

        return sortedOffers.stream()
                .map(offer -> new OfferResponseDTO(
                        offer.getId(),
                        offer.getStore() != null ? offer.getStore().getId() : null,
                        offer.getStore() != null ? offer.getStore().getName() : null,
                        offer.getStore() != null ? offer.getStore().getLogoUrl() : null,
                        offer.getPrice(),
                        offer.getAvailable(),
                        offer.getUrl(),
                        offer.getPrice() != null && cheapest != null && Double.compare(offer.getPrice(), cheapest) == 0
                ))
                .toList();
    }

    // =========================================================
    //  SMART PRICE COMPARISON ENGINE (FYP CORE FEATURE)
    // =========================================================
    @Override
    public Map<String, Object> comparePrices(UUID productId) {

        log.info("[OfferService] Comparing prices for productId={}", productId);

        List<Offer> offers = offerRepository.findByProduct_IdAndAvailableTrue(productId);

        if (offers == null || offers.isEmpty()) {
            log.warn("[OfferService] No offers found for productId={}", productId);
            return Map.of(
                    "message", "No offers available for this product"
            );
        }

        // sort by price
        List<Offer> sorted = offers.stream()
                .sorted(Comparator.comparingDouble(Offer::getPrice))
                .collect(Collectors.toList());

        Offer cheapest = sorted.get(0);
        Offer highest = sorted.get(sorted.size() - 1);

        double avg = offers.stream()
                .mapToDouble(Offer::getPrice)
                .average()
                .orElse(0);

        double diffPercent = ((avg - cheapest.getPrice()) / avg) * 100;

        String message;

        if (diffPercent > 10) {
            message = " Excellent deal! Significantly cheaper than market average";
        } else if (diffPercent > 5) {
            message = " Good deal compared to other stores";
        } else {
            message = " Price is close to market average";
        }

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("product", cheapest.getProduct().getTitle());
        response.put("best_price", cheapest.getPrice());
        response.put("best_store", cheapest.getStore().getName());

        response.put("highest_price", highest.getPrice());
        response.put("highest_store", highest.getStore().getName());

        response.put("average_price", Math.round(avg));
        response.put("difference_percent", Math.round(diffPercent));
        response.put("message", message);

        log.info("[OfferService] Comparison completed successfully for productId={}", productId);

        return response;
    }

    // -------------------- MAPPER --------------------
    private OfferDTO toDto(Offer offer) {
        if (offer == null) return null;

        return new OfferDTO(
                offer.getId(),
                offer.getPrice(),
                offer.getAvailable(),
                offer.getUrl(),
                offer.getFetchedAt(),
                offer.getProduct() != null ? offer.getProduct().getId() : null,
                offer.getProduct() != null ? offer.getProduct().getTitle() : null,
                offer.getStore() != null ? offer.getStore().getId() : null,
                offer.getStore() != null ? offer.getStore().getName() : null
        );
    }
}