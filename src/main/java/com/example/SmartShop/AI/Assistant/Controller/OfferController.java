package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
import com.example.SmartShop.AI.Assistant.Service.OfferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing Offers.
 * Supports pagination, filtering, and AI price comparison.
 */
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private static final Logger log = LoggerFactory.getLogger(OfferController.class);

    private final OfferService offerService;

    @GetMapping
    public ResponseEntity<Page<OfferDTO>> getAllOffers(Pageable pageable) {

        log.info("[OfferController] GET all offers | page={} size={}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        return ResponseEntity.ok(offerService.getAllOffers(pageable));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<OfferDTO>> getOffersByProduct(
            @PathVariable UUID productId,
            Pageable pageable) {

        log.info("[OfferController] GET offers by productId={}", productId);

        return ResponseEntity.ok(offerService.getOffersByProduct(productId, pageable));
    }

    @GetMapping("/product/{productId}/cheapest")
    public ResponseEntity<OfferDTO> getCheapestOffer(@PathVariable UUID productId) {

        log.info("[OfferController] GET cheapest offer | productId={}", productId);

        return offerService.getCheapestOffer(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("[OfferController] No cheapest offer found | productId={}", productId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/search")
    public ResponseEntity<Page<OfferDTO>> searchOffers(
            @RequestParam String query,
            @RequestParam(required = false) Double budget,
            Pageable pageable) {

        log.info("[OfferController] SEARCH offers | query='{}' budget={}", query, budget);

        return ResponseEntity.ok(
                offerService.searchOffers(query, budget, pageable)
        );
    }

    // =========================================================
    //  SMART PRICE COMPARISON (AI ENGINE)
    // =========================================================
    @GetMapping("/product/{productId}/compare")
    public ResponseEntity<Map<String, Object>> comparePrices(
            @PathVariable UUID productId) {

        log.info("[OfferController] PRICE COMPARISON request | productId={}", productId);

        Map<String, Object> result = offerService.comparePrices(productId);

        if (result == null || result.isEmpty()) {
            log.warn("[OfferController] No comparison data found | productId={}", productId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}