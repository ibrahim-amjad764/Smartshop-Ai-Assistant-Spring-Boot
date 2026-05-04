package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.OfferResponseDTO;
import com.example.SmartShop.AI.Assistant.Dto.StoreDetailDTO;
import com.example.SmartShop.AI.Assistant.Dto.StoreRatingDTO;
import com.example.SmartShop.AI.Assistant.Dto.StoreSummaryDTO;
import com.example.SmartShop.AI.Assistant.Service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public ResponseEntity<Page<StoreSummaryDTO>> getStores(Pageable pageable) {
        return ResponseEntity.ok(storeService.getAllStores(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDetailDTO> getStoreById(@PathVariable UUID id) {
        return ResponseEntity.ok(storeService.getStoreDetails(id));
    }

    @GetMapping("/{id}/offers")
    public ResponseEntity<Page<OfferResponseDTO>> getStoreOffers(@PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok(storeService.getStoreOffers(id, pageable));
    }

    @GetMapping("/{id}/rating")
    public ResponseEntity<StoreRatingDTO> getStoreRating(@PathVariable UUID id) {
        return ResponseEntity.ok(storeService.getStoreRating(id));
    }
}
