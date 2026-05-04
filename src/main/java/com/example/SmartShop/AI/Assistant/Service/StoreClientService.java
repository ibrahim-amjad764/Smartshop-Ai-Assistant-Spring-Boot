package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
import com.example.SmartShop.AI.Assistant.Entity.Offer;
import com.example.SmartShop.AI.Assistant.Entity.Store;
import com.example.SmartShop.AI.Assistant.Repository.OfferRepository;
import com.example.SmartShop.AI.Assistant.Repository.StoreRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // performance optimization for read-only queries
public class StoreClientService {

    private static final Logger log = LoggerFactory.getLogger(StoreClientService.class);

    private final OfferRepository offerRepository;
    private final StoreRepository storeRepository;

    public StoreClientService(OfferRepository offerRepository,
                              StoreRepository storeRepository) {
        this.offerRepository = offerRepository;
        this.storeRepository = storeRepository;
    }

    // =========================================================
    // FETCH OFFERS BY STORE NAME OR PRODUCT TITLE
    // =========================================================
    public List<OfferDTO> fetchOffers(String query) {

        if (query == null || query.isBlank()) {
            log.warn("[StoreClientService] Query is null or blank.");
            return List.of(); // safe empty immutable list
        }

        log.info("[StoreClientService] Fetching offers for query: {}", query);

        List<Offer> offers =
                offerRepository
                        .findByProduct_TitleContainingIgnoreCaseOrStore_NameContainingIgnoreCase(query, query);

        log.info("[StoreClientService] Total offers fetched from DB: {}", offers.size());

        return offers.stream()
                .map(this::mapToOfferDTO)
                .toList(); // Java 17 optimized
    }

    // =========================================================
    // FETCH ALL OFFERS
    // =========================================================
    public List<OfferDTO> fetchAllOffers() {

        log.info("[StoreClientService] Fetching ALL offers from DB");

        List<Offer> offers = offerRepository.findAll();

        log.info("[StoreClientService] Total offers found: {}", offers.size());

        return offers.stream()
                .map(this::mapToOfferDTO)
                .toList();
    }

    // =========================================================
    // FETCH ALL STORES
    // =========================================================
    public List<Store> fetchAllStores() {

        log.info("[StoreClientService] Fetching ALL stores from DB");

        List<Store> stores = storeRepository.findAll();

        log.info("[StoreClientService] Total stores found: {}", stores.size());

        return stores;
    }

    // =========================================================
    // PRIVATE MAPPER (Single Responsibility Principle)
    // =========================================================
    private OfferDTO mapToOfferDTO(Offer offer) {

        return new OfferDTO(
                offer.getId(),
                offer.getPrice(),
                offer.getAvailable(),
                offer.getUrl(),
                offer.getFetchedAt(),
                offer.getProduct().getId(),
                offer.getProduct().getTitle(),
                offer.getStore().getId(),
                offer.getStore().getName()
        );
    }
}

//package com.example.SmartShop.AI.Assistant.Service;
//
//import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class StoreClientService {
//
//    private static final Logger log = LoggerFactory.getLogger(StoreClientService.class); // ✅ Logger added
//
//    private final RestTemplate restTemplate;
//
//    public StoreClientService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public List<OfferDTO> fetchDummyProducts(String query) {
//        List<OfferDTO> result = new ArrayList<>();
//
//        try {
//            log.info("Fetching dummy products for: {}", query);
//
//            String url = "https://dummyjson.com/products/search?q=" + query;
//            Map response = restTemplate.getForObject(url, Map.class);
//
//            if (response == null || !response.containsKey("products")) {
//                log.warn("No products found for query: {}", query);
//                return result;
//            }
//
//            List<Map> products = (List<Map>) response.get("products");
//            for (Map p : products) {
//                OfferDTO dto = new OfferDTO();
//                dto.setTitle((String) p.get("title"));
//                dto.setPrice(Double.parseDouble(p.get("price").toString()));
//                dto.setStoreName("DummyStore");
//                dto.setUrl("https://dummyjson.com/products/" + p.get("id"));
//                result.add(dto);
//            }
//
//            log.info("Found {} products for {}", result.size(), query);
//
//        } catch (Exception e) {
//            log.error("Error fetching products for '{}': {}", query, e.getMessage(), e);
//        }
//
//
//        return result;
//    }
//}
