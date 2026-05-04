package com.example.SmartShop.AI.Assistant.Controller;

import com.example.SmartShop.AI.Assistant.Dto.AIProductRecommendationDTO;
import com.example.SmartShop.AI.Assistant.Dto.PersonalizationContextDTO;
import com.example.SmartShop.AI.Assistant.Repository.UserRepository;
import com.example.SmartShop.AI.Assistant.Service.AIServiceImpl;
import com.example.SmartShop.AI.Assistant.Service.AIService;
import com.example.SmartShop.AI.Assistant.Service.SearchQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AIController {

    private final AIService aiService;
    private final AIServiceImpl aiServiceImpl;
    private final UserRepository userRepository;
    private final SearchQueryService searchQueryService;

    public AIController(
            AIService aiService,
            AIServiceImpl aiServiceImpl,
            UserRepository userRepository,
            SearchQueryService searchQueryService
    ) {
        this.aiService = aiService;
        this.aiServiceImpl = aiServiceImpl;
        this.userRepository = userRepository;
        this.searchQueryService = searchQueryService;
    }

    // ========================= SMART AI SEARCH =========================
    /**
     *  NEW: ChatGPT-style product search
     * POST /api/ai/smart-search
     * Body: { "query": "best gaming phone under 50000" }
     *
     * Returns: Products ranked by AI with reasoning
     */
    @PostMapping("/ai/smart-search")
    public ResponseEntity<?> smartSearch(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        Double budget = null;
        try {
            budget = request.get("budget") != null ? Double.valueOf(request.get("budget")) : null;
        } catch (NumberFormatException ignored) {}
        System.out.println(" Smart search: " + query);

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query required"));
        }

        try {
            var user = resolveCurrentUser();
            PersonalizationContextDTO context = user != null ? aiServiceImpl.buildPersonalizationContext(user) : null;
            searchQueryService.log(query, budget, user != null ? user.getId() : null);
            List<AIProductRecommendationDTO> results = aiService.smartProductSearch(query, budget, context);

            return ResponseEntity.ok(Map.of(
                    "query", query,
                    "count", results.size(),
                    "results", results,
                    "message", results.isEmpty()
                            ? "No direct matches — try broader keywords or adjust budget/specs"
                            : "AI recommendations ready"
            ));

        } catch (Exception e) {
            System.out.println(" Smart search error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "AI search failed: " + e.getMessage()));
        }
    }

    private com.example.SmartShop.AI.Assistant.Entity.User resolveCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }
        String email = String.valueOf(auth.getPrincipal());
        return userRepository.findByEmail(email).orElse(null);
    }

    // ========================= QUICK SUGGESTIONS =========================
    @GetMapping("/ai/suggest")
    public List<String> suggestProducts(@RequestParam String query) {
        System.out.println("DEBUG: /api/ai/suggest called: " + query);

        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return aiService.suggestProducts(query);
        } catch (Exception e) {
            System.out.println("ERROR in /suggest: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ========================= CHAT RECOMMENDATION =========================

    @PostMapping({"/chat", "/ai/chat"})
    public ResponseEntity<?> chatRecommendation(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        // Basic input validation
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message is required"));
        }

        String trimmedMessage = message.trim();
        System.out.println("INFO: /api/chat called with message length = " + trimmedMessage.length());

        try {
            List<AIProductRecommendationDTO> ranked = aiService.smartProductSearch(trimmedMessage);
            String recommendation = aiService.buildAssistantResponse(trimmedMessage, ranked);

            if (recommendation == null || recommendation.trim().isEmpty()) {
                System.out.println("WARN: AI service returned empty recommendation");
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "AI service returned an empty response"));
            }

            String finalRecommendation = recommendation.trim();
            System.out.println("INFO: AI recommendation length = " + finalRecommendation.length());

            return ResponseEntity.ok(Map.of(
                    "message", trimmedMessage,
                    "recommendation", finalRecommendation
            ));

        } catch (Exception e) {
            System.out.println("ERROR: Chat recommendation failed: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to get AI recommendation. Please try again later."));
        }
    }
}