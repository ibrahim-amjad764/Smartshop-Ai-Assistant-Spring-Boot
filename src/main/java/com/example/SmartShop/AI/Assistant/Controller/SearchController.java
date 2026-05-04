package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.ProductDTO;
import com.example.SmartShop.AI.Assistant.Dto.SearchQueryLogRequestDTO;
import com.example.SmartShop.AI.Assistant.Dto.SearchResponseDTO;
import com.example.SmartShop.AI.Assistant.Dto.StoreSummaryDTO;

import com.example.SmartShop.AI.Assistant.Service.AIService;
import com.example.SmartShop.AI.Assistant.Service.OfferService;
import com.example.SmartShop.AI.Assistant.Service.ProductService;
import com.example.SmartShop.AI.Assistant.Service.SearchQueryService;
import com.example.SmartShop.AI.Assistant.Service.StoreService;

import jakarta.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final OfferService offerService;
    private final AIService aiService;
    private final StoreService storeService;
    private final ProductService productService;
    private final SearchQueryService searchQueryService;

    public SearchController(
            OfferService offerService,
            AIService aiService,
            StoreService storeService,
            ProductService productService,
            SearchQueryService searchQueryService
    ) {
        this.offerService = offerService;
        this.aiService = aiService;
        this.storeService = storeService;
        this.productService = productService;
        this.searchQueryService = searchQueryService;
    }

    // ---------------- SEARCH PRODUCTS ----------------
    @GetMapping("/search")
    public SearchResponseDTO search(
            @RequestParam @NotBlank String query,
            @RequestParam(required = false) Double budget,
            Pageable pageable
    ) {

        log.info("Search request: query={}, budget={}", query, budget);
        searchQueryService.log(query, budget, null);

        var offersPage = offerService.searchOffers(query, budget, pageable);
        var offers = offersPage.getContent();

        if (offers.isEmpty()) {
            return new SearchResponseDTO(
                    Collections.emptyList(),
                    "No products found."
            );
        }

        String suggestion = aiService.getRecommendation(
                "User searched for '" + query + "'" +
                        (budget != null ? " with budget " + budget : ""),
                offers
        );

        if (suggestion == null) {
            suggestion = "No recommendation available.";
        }

        return new SearchResponseDTO(offers, suggestion);
    }

    // ---------------- SEARCH SUGGESTIONS ----------------
    @GetMapping("/search/suggestions")
    public List<ProductDTO> getSuggestions(
            @RequestParam @NotBlank String query
    ) {

        log.info("Search suggestions for query={}", query);

        try {
            return productService.toDtoList(
                    productService.findTop10ByTitleContainingIgnoreCase(query)
            );
        } catch (Exception e) {
            log.error("Error fetching suggestions", e);
            return Collections.emptyList();
        }
    }

    @PostMapping("/search/events")
    public void logSearchQuery(@RequestBody SearchQueryLogRequestDTO request) {
        searchQueryService.log(request.queryText(), request.userBudget(), null);
    }

    @GetMapping("/search/trending")
    public List<java.util.Map<String, Object>> getTrendingSearches(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return searchQueryService.getTrending(limit);
    }
    // ---------------- HEALTH CHECK ----------------
    @GetMapping("/health")
    public String health() {
        return "SmartShop Backend Running ✅";
    }
}