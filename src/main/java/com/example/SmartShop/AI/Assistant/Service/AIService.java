package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.AIProductRecommendationDTO;
import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
import com.example.SmartShop.AI.Assistant.Dto.PersonalizationContextDTO;
import com.example.SmartShop.AI.Assistant.Dto.UserQueryDTO;

import java.util.List;

/**
 * AIService interface declares methods for AI-powered suggestions and search.
 *
 *  Production Notes:
 * - Keep interface clean (no implementation logic)
 * - Used for loose coupling (Controller ↔ Service)
 * - Easy to extend (future AI features)
 */
public interface AIService {

    /**
     *  Phase 2: Convert natural query → structured AI intent
     */
    UserQueryDTO parseUserQuery(String query);

    /**
     * Intelligent product search with AI ranking (catalog-backed).
     */
    List<AIProductRecommendationDTO> smartProductSearch(String query);

    default List<AIProductRecommendationDTO> smartProductSearch(
            String query,
            Double userBudget,
            PersonalizationContextDTO personalization
    ) {
        return smartProductSearch(query);
    }

    /**
     * Natural-language reply grounded in {@code products} only (for chat UI).
     */
    String buildAssistantResponse(String userMessage, List<AIProductRecommendationDTO> products);

    /**
     *  Suggest product names based on user query
     *
     * Example:
     * input: "shoes"
     * output: ["Nike Shoes", "Adidas Sneakers"]
     *
     * @param query user search input
     * @return list of suggested product names (never null)
     */
    List<String> suggestProducts(String query);

    /**
     *  AI-powered product search
     *
     * More refined than suggestions (intent-based)
     *
     * @param query user search input
     * @return list of matching product names (never null)
     */
    List<String> searchProducts(String query);

    /**
     *  Generate smart recommendation message
     *
     * Example:
     * input: "budget phone"
     * output: "Consider mid-range Samsung or Xiaomi devices..."
     *
     * @param context user intent or search context
     * @return recommendation string (never null)
     */
    String getRecommendation(String context);

    /**
     * Shopping guidance anchored to the current search result offers only.
     */
    String getRecommendation(String queryContext, List<OfferDTO> offers);

    // ========================= FUTURE READY METHODS =========================

    /**
     *  OPTIONAL (Future Upgrade)
     * Chat-based AI (like ChatGPT-style conversation)
     *
     * Not implemented yet — keep for scalability
     *
     * @param message user message
     * @return AI response
     */
    default String chat(String message) {
        System.out.println("WARNING: chat() not implemented yet");
        return "Chat feature coming soon.";
    }
}

