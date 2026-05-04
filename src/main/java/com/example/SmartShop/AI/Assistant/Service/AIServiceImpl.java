package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Dto.AIProductRecommendationDTO;
import com.example.SmartShop.AI.Assistant.Dto.OfferDTO;
import com.example.SmartShop.AI.Assistant.Dto.PersonalizationContextDTO;
import com.example.SmartShop.AI.Assistant.Entity.CartItem;
import com.example.SmartShop.AI.Assistant.Entity.Favorite;
import com.example.SmartShop.AI.Assistant.Entity.Product;
import com.example.SmartShop.AI.Assistant.Entity.SearchQuery;
import com.example.SmartShop.AI.Assistant.Entity.User;
import com.example.SmartShop.AI.Assistant.Repository.CartRepository;
import com.example.SmartShop.AI.Assistant.Repository.FavoriteRepository;
import com.example.SmartShop.AI.Assistant.Repository.ProductRepository;
import com.example.SmartShop.AI.Assistant.Repository.SearchQueryRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.example.SmartShop.AI.Assistant.Dto.UserQueryDTO;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Enhanced AIService with intelligent product search.
 * Combines database search + AI ranking for ChatGPT-style recommendations.
 */
@Service
public class AIServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);
    private static final int MAX_RETRIES = 3;


    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @PostConstruct
    public void init() {
        log.info("API KEY loaded successfully");
    }

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final CartRepository cartRepository;
    private final SearchQueryRepository searchQueryRepository;

    public AIServiceImpl(
            RestTemplate restTemplate,
            ProductRepository productRepository,
            FavoriteRepository favoriteRepository,
            CartRepository cartRepository,
            SearchQueryRepository searchQueryRepository
    ) {
        this.restTemplate = restTemplate;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        this.cartRepository = cartRepository;
        this.searchQueryRepository = searchQueryRepository;
    }

    // ========================= SMART AI SEARCH =========================
    /**
     *  NEW: Intelligent product search with AI ranking
     *
     * Flow:
     * 1. Fetch top 20 matching products from database (by title/brand)
     * 2. Send products + user query to AI for intelligent ranking
     * 3. Return top 5 products with AI reasoning
     */
    @Override
    public List<AIProductRecommendationDTO> smartProductSearch(String query) {
        return smartProductSearch(query, null, null);
    }

    @Override
    public List<AIProductRecommendationDTO> smartProductSearch(
            String query,
            Double userBudget,
            PersonalizationContextDTO personalization
    ) {

        log.info(" SmartProductSearch called: {}", query);

        UserQueryDTO intent = parseUserQuery(query);

        log.info(" Extracted Intent → {}", intent);

        String searchKey = buildSearchKey(query, intent);

        List<Product> candidates = fetchProductCandidates(searchKey);

        if (userBudget != null) {
            candidates = candidates.stream()
                    .filter(p -> p.getPrice() == null || p.getPrice() <= userBudget)
                    .toList();
        }

        log.info(" Found {} candidate products", candidates.size());

        if (candidates == null || candidates.isEmpty()) {

            log.info(" No DB products → using pure AI fallback");

            String aiSuggestion = callOpenAI(
                    """
                    User searched for: "%s"
                    
                    You are SmartShop AI — a STRICT fallback retail intelligence engine.
                    
                    =================================================
                     CORE PURPOSE
                    =================================================
                    The requested product is NOT available in the catalog.
                    
                    You are NOT a chatbot.
                    You are NOT a conversational assistant.
                    You are a structured retail insight generator.
                    
                    Your job:
                    Give general, real-world buying guidance WITHOUT pretending products exist.
                    
                    =================================================
                     ABSOLUTE RULES (HARD CONSTRAINTS)
                    =================================================
                    - NEVER ask any questions
                    - NEVER suggest refining search
                    - NEVER say "no results"
                    - NEVER apologize or express regret
                    - NEVER mention AI, system, or limitations
                    - NEVER invent products, brands, or models
                    - NEVER simulate catalog results
                    - NEVER act conversational or chatty
                    
                    =================================================
                     THINKING PROCESS (INTERNAL ONLY)
                    =================================================
                    Step 1: Identify category intent from query
                    Step 2: Extract key decision factors (what matters in this category)
                    Step 3: Provide real-world buying guidance (generic but useful)
                    Step 4: End naturally without CTA or question
                    
                    DO NOT reveal steps in output.
                    
                    =================================================
                     OUTPUT RULES
                    =================================================
                    - 2 to 4 sentences ONLY
                    - No bullet points
                    - No headings
                    - No formatting
                    - No repetition
                    - No filler words
                    - No conversational tone
                    
                    =================================================
                     RESPONSE STYLE
                    =================================================
                    - Neutral, confident, factual tone
                    - Like a retail market analyst report
                    - No emotional language
                    - No engagement hooks
                    
                    =================================================
                     GOOD RESPONSE PATTERNS
                    =================================================
                    
                    Gaming laptop:
                    "Gaming laptops are primarily evaluated based on GPU performance, processor strength, and cooling efficiency. Higher specifications generally ensure smoother gameplay and better multitasking capability. Long-term performance depends on balanced hardware rather than single components."
                    
                    Cheap phone:
                    "Budget smartphones are typically defined by battery efficiency, display quality, and optimized performance. Practical usage depends more on system stability than high-end specifications. Reliable devices focus on essential daily tasks without unnecessary features."
                    
                    Headphones:
                    "Headphones are selected based on sound quality, comfort, and durability. Wireless stability and battery life are important for long usage. Balanced audio performance is more valuable than extreme specifications for everyday use."
                    
                    =================================================
                     NOW RESPOND
                    =================================================
                    """.formatted(query),
                    AiCallType.ASSISTANT
            );

            return List.of(
                    AIProductRecommendationDTO.builder()
                            .title("No direct match found")
                            .aiScore(5)
                            .aiReason(aiSuggestion != null && !aiSuggestion.isBlank()
                                    ? aiSuggestion
                                    : "Try using different keywords or exploring similar product categories.")
                            .build()
            );
        }

// Decide ranking strategy
        if (shouldUseAI(candidates, intent, query)) {

            log.info(" Using AI ranking engine (external)");

            List<AIProductRecommendationDTO> aiResults = rankWithAI(query, candidates);

            // Fallback safety: if AI fails or returns empty
            if (aiResults == null || aiResults.isEmpty()) {
                log.warn(" AI ranking failed → falling back to local ranking");
            } else {
                return aiResults;
            }
        }

        log.info("⚡ Using local ranking (fast mode)");

        return candidates.stream()
                .limit(5)
                .map(p -> AIProductRecommendationDTO.builder()
                        .productId(p.getId())
                        .title(p.getTitle())
                        .brand(p.getBrand())
                        .price(p.getPrice())
                        .imageUrl(p.getImageUrl())
                        .model(p.getModel())
                        .ram(p.getRam())
                        .storage(p.getStorage())
                        .aiScore(calculateScore(query, p, personalization, userBudget))
                        .aiReason(generateLocalReason(query, p))
                        .build()
                )
                .collect(Collectors.toList());
    }

    private int calculateScore(String query, Product p, PersonalizationContextDTO personalization, Double userBudget) {

        int score = 5;

        String q = query.toLowerCase();

        Double price = p.getPrice() != null ? p.getPrice() : 999999;

        //  Budget / Cheap logic (HIGH IMPACT)
        if (q.contains("cheap") || q.contains("budget")) {
            score += (100000 - price.intValue()) / 20000; // lower price = higher score
        }

        // 🎮 Gaming logic
        if (q.contains("gaming")) {
            score += 2;

            if (p.getRam() != null && p.getRam().toLowerCase().contains("16")) {
                score += 2;
            } else if (p.getRam() != null && p.getRam().toLowerCase().contains("8")) {
                score += 1;
            }
        }

        //  Camera logic
        if (q.contains("camera")) {
            score += 2;
        }

        //  General phone/laptop boost
        if (q.contains("phone") || q.contains("laptop")) {
            score += 1;
        }

        if (p.getBrand() != null && p.getBrand().toLowerCase().contains("apple")) {
            score += 1;
        }

        if (userBudget != null && p.getPrice() != null && p.getPrice() <= userBudget) {
            score += 1;
        }

        if (personalization != null) {
            if (personalization.favoriteProductIds() != null
                    && personalization.favoriteProductIds().contains(p.getId())) {
                score += 2;
            }
            if (personalization.cartProductIds() != null
                    && personalization.cartProductIds().contains(p.getId())) {
                score += 2;
            }
            if (personalization.recentSearches() != null) {
                String brand = p.getBrand() != null ? p.getBrand().toLowerCase() : "";
                String category = p.getCategory() != null ? p.getCategory().name().toLowerCase() : "";
                boolean matchesHistory = personalization.recentSearches().stream()
                        .map(String::toLowerCase)
                        .anyMatch(s -> (!brand.isBlank() && s.contains(brand)) || (!category.isBlank() && s.contains(category)));
                if (matchesHistory) {
                    score += 1;
                }
            }
        }

        return Math.min(Math.max(score, 1), 10);
    }

    public PersonalizationContextDTO buildPersonalizationContext(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }

        List<UUID> favoriteIds = favoriteRepository.findAllByUser(user).stream()
                .map(Favorite::getProduct)
                .filter(Objects::nonNull)
                .map(Product::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<UUID> cartIds = cartRepository.findAllByUser(user).stream()
                .map(CartItem::getProduct)
                .filter(Objects::nonNull)
                .map(Product::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<String> recentSearches = searchQueryRepository
                .findTop10ByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(SearchQuery::getQueryText)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return new PersonalizationContextDTO(user.getId(), favoriteIds, cartIds, recentSearches);
    }

    private String buildSearchKey(String query, UserQueryDTO intent) {

        String q = query.toLowerCase();

        if (q.contains("gaming")) return "gaming laptop";
        if (q.contains("cheap") || q.contains("budget")) return "low price";
        if (q.contains("camera")) return "camera phone";
        if (q.contains("headphone")) return "headphone";
        if (q.contains("laptop")) return "laptop";
        if (q.contains("phone")) return "phone";

        return query;
    }

    private String generateLocalReason(String query, Product p) {
        return "Matches your search for \"" + query + "\" with relevant features and a good overall fit.";
    }

    @Override
    public UserQueryDTO parseUserQuery(String query) {

        log.info(" [AI] Parsing user query into structured intent: {}", query);

        String prompt = """
INTENT EXTRACTION TASK — output ONE JSON object only.

Extract from the user query:
- budget (number or null)
- category (string or null)
- usage (string or null)
- preferences (string or null)

Rules:
- If a field is missing or unclear → null (never guess fake numbers or categories)
- Output ONLY valid JSON — no markdown, no explanation

User Query:
""" + query;

        String response = callOpenAI(prompt, AiCallType.STRUCTURED);

// Clean response (IMPORTANT)
        response = response
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        log.info(" [AI] Raw intent response: {}", response);

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();

            // extract only JSON part
            String jsonOnly = response.substring(
                    response.indexOf("{"),
                    response.lastIndexOf("}") + 1
            );

            UserQueryDTO dto = mapper.readValue(jsonOnly, UserQueryDTO.class);

            log.info(" Parsed Intent → {}", dto);

            return dto;

        } catch (Exception e) {
            log.error(" Failed parsing AI intent: {}", e.getMessage());
            return new UserQueryDTO();
        }
    }
    /**
     * Fetch product candidates from database
     */
    private List<Product> fetchProductCandidates(String query) {
        // Search by title (case-insensitive)
        List<Product> byTitle = productRepository
                .findTop20ByTitleContainingIgnoreCase(query);

        // Search by brand
        List<Product> byBrand = productRepository
                .findTop10ByBrandContainingIgnoreCase(query);

        // Combine and deduplicate
        Set<Product> unique = new LinkedHashSet<>();
        unique.addAll(byTitle);
        unique.addAll(byBrand);

        return new ArrayList<>(unique);
    }

    /**
     * Send products to AI for intelligent ranking
     */
    private List<AIProductRecommendationDTO> rankWithAI(String userQuery, List<Product> products) {

        // Build product context for AI
        String productContext = products.stream()
                .map(p -> String.format(
                        "ID:%s | Title:%s | Brand:%s | Price:%.0f | Specs:%s %s %s",
                        p.getId(),
                        p.getTitle(),
                        p.getBrand(),
                        p.getPrice() != null ? p.getPrice() : 0,
                        p.getModel() != null ? p.getModel() : "",
                        p.getRam() != null ? p.getRam() : "",
                        p.getStorage() != null ? p.getStorage() : ""
                ))
                .collect(Collectors.joining("\n"));

        String prompt = String.format("""
RANKING TASK — output ONLY a JSON array (no markdown, no text outside the array).

User Query:
"%s"

Products (use ONLY these IDs from the list; never invent productId):
%s

CORE RULE:
You are a strict product ranking engine. Your only job is to rank given products based on relevance to the user query.

--------------------------------------------------
SCORING LOGIC (VERY IMPORTANT):

1. INTENT PRIORITY RULES:

- If query contains "cheap" or "budget":
    → PRIORITIZE LOWEST PRICE FIRST (price is the most important factor)

- If query contains "gaming":
    → PRIORITIZE PERFORMANCE:
       (RAM > processor > GPU > overall speed)

- If query contains "camera":
    → PRIORITIZE CAMERA QUALITY FEATURES

- If query contains "premium" or "best":
    → PRIORITIZE OVERALL QUALITY (not price)

--------------------------------------------------
2. RANKING RULES:

- Select TOP 5 most relevant products only
- Order them from best match → weakest match
- Score must be between 1–10
- Use REAL product attributes only (price, RAM, storage, brand, model)
- Do NOT invent or assume features not present in input

--------------------------------------------------
3. REASONING RULES:

- Reasons must be short, natural, and human-like
- Avoid repetitive sentence structure
- Focus on WHY it matches the query
- Mention tradeoffs when relevant (price vs performance, etc.)
- Do NOT be generic like "good product"

--------------------------------------------------
4. CONSISTENCY RULES:

- Do NOT repeat same reasoning style across products
- Vary sentence structure
- Be direct and confident
- No storytelling, no fluff

--------------------------------------------------
OUTPUT FORMAT (STRICT JSON ONLY):

[
  {
    "productId": "uuid-here",
    "score": 9,
    "reason": "Excellent gaming performance with high RAM and strong processor, offering great value."
  }
]
""", userQuery, productContext);

        String aiResponse = callOpenAI(prompt, AiCallType.STRUCTURED);
        // Parse AI response to get ranked IDs
        List<AIProductRank> ranks = parseAIRanking(aiResponse);

        // Map back to products with reasoning
        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return ranks.stream()
                .map(rank -> {
                    Product p = productMap.get(UUID.fromString(rank.productId));
                    if (p == null) return null;

                    return AIProductRecommendationDTO.builder()
                            .productId(p.getId())
                            .title(p.getTitle())
                            .brand(p.getBrand())
                            .price(p.getPrice())
                            .imageUrl(p.getImageUrl())
                            .model(p.getModel())
                            .ram(p.getRam())
                            .storage(p.getStorage())
                            .aiScore(rank.score)
                            .aiReason(rank.reason)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(AIProductRecommendationDTO::getAiScore).reversed())
                .collect(Collectors.toList());
    }

    // ========================= AI QUICK SUGGESTIONS =========================
    @Override
    public List<String> suggestProducts(String query) {
        log.info("SuggestProducts: {}", query);

        if (openRouterApiKey == null || openRouterApiKey.isBlank()) {
            log.error(" OpenRouter API key is missing!");
            return List.of("AI service is not configured properly.");
        }

        List<Product> catalog = new ArrayList<>(
                productRepository.findTop10ByTitleContainingIgnoreCase(query != null ? query : ""));
        if (catalog.isEmpty()) {
            catalog = productRepository.findTop5ByOrderByCreatedAtDesc();
        }
        if (catalog.isEmpty()) {
            return List.of();
        }

        String productTitles = catalog.stream()
                .map(Product::getTitle)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining("\n"));

        String prompt = """
User search hint:
""" + (query != null ? query : "") + """

Product list (pick from these titles only):
""" + productTitles;

        System.out.println("[SmartShop AI] suggestProducts catalogLines=" + catalog.size());
        String result = callOpenAI(prompt, AiCallType.SUGGEST);
        Set<String> allowedLower = catalog.stream()
                .map(Product::getTitle)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<String> parsed = splitToList(result);
        List<String> safe = parsed.stream()
                .filter(s -> allowedLower.contains(s.toLowerCase()))
                .distinct()
                .limit(5)
                .collect(Collectors.toList());

        if (!safe.isEmpty()) {
            return safe;
        }
        return catalog.stream()
                .map(Product::getTitle)
                .filter(Objects::nonNull)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }

    private boolean shouldUseAI(List<Product> products, UserQueryDTO intent, String query) {

        log.info(" [AI Decision Engine] Evaluating AI need...");

        // Rule 1: No products → no AI needed
        if (products == null || products.isEmpty()) {
            log.info(" No products found → skipping AI");
            return false;
        }

        // Rule 2: Very few results → fast mode
        if (products.size() < 5) {
            log.info("⚡ Few results (<5) → fast mode");
            return false;
        }

        // Rule 3: Complex query detection
        boolean isComplexQuery =
                query != null && (
                        query.toLowerCase().contains("best") ||
                                query.toLowerCase().contains("compare") ||
                                query.toLowerCase().contains("vs") ||
                                query.toLowerCase().contains("recommend") ||
                                query.toLowerCase().contains("budget") ||
                                query.toLowerCase().contains("gaming")
                );

        // Rule 4: Missing AI intent data
        boolean missingIntent =
                intent == null ||
                        intent.getBudget() == null ||
                        intent.getCategory() == null;

        // Rule 5: Large dataset → AI improves ranking
        boolean largeDataset = products.size() > 10;

        boolean useAI = isComplexQuery || missingIntent || largeDataset;

        log.info(" AI Decision Breakdown:");
        log.info("   - Complex Query: {}", isComplexQuery);
        log.info("   - Missing Intent: {}", missingIntent);
        log.info("   - Large Dataset: {}", largeDataset);
        log.info(" FINAL DECISION → USE AI: {}", useAI);

        return useAI;
    }

    @Override
    public List<String> searchProducts(String query) {
        // Delegate to smart search but just return titles
        return smartProductSearch(query).stream()
                .map(AIProductRecommendationDTO::getTitle)
                .collect(Collectors.toList());
    }

    @Override
    public String getRecommendation(String context) {
        if (openRouterApiKey == null || openRouterApiKey.isBlank()) {
            return "Explore our latest products!";
        }

        String prompt = """
Help the shopper using ONLY what they said in the context below. Do not invent products or prices.
If they named items, you may reference those names only. Keep it to 2–3 short sentences. Compare options only if the context gives multiple.
Focus on budget, specs, and usage where relevant.

Context:
""" + context;

        return callOpenAI(prompt, AiCallType.ASSISTANT);
    }

    @Override
    public String getRecommendation(String queryContext, List<OfferDTO> offers) {
        if (openRouterApiKey == null || openRouterApiKey.isBlank()) {
            return "Explore our latest products!";
        }
        if (offers == null || offers.isEmpty()) {
            return getRecommendation(queryContext);
        }

        String offerBlock = offers.stream()
                .map(o -> String.format(
                        "- %s @ %s | price:%s",
                        o.productTitle() != null ? o.productTitle() : "Product",
                        o.storeName() != null ? o.storeName() : "Store",
                        o.price() != null ? o.price() : "n/a"))
                .collect(Collectors.joining("\n"));

        String prompt = """
Search context:
""" + queryContext + """

Catalog (ONLY discuss these offers — do not add others):
""" + offerBlock + """

In 3–4 short sentences: best picks, comparisons where useful, budget/spec tradeoffs. Natural tone, not robotic.
""";

        return callOpenAI(prompt, AiCallType.ASSISTANT);
    }

    @Override
    public String buildAssistantResponse(String userMessage, List<AIProductRecommendationDTO> products) {
        if (openRouterApiKey == null || openRouterApiKey.isBlank()) {
            return "Try exploring products with the search bar!";
        }
        if (products == null || products.isEmpty()) {
            return callOpenAI(
                    """
User message: "%s"

This question doesn’t map cleanly to products in our current catalog list.

Your task:
- Do NOT invent products
- Avoid “no results found” wording
- Give simple, real-world guidance for what they asked (category-level tips)
- If the user wants recommendations, explain that you can suggest options when products are provided
  (do not ask unnecessary questions; keep it brief)
- Do not force the user to refine; tips should be optional

Keep it short, practical, and helpful. Give the solution first.
""".formatted(userMessage),
                    AiCallType.ASSISTANT
            );
        }

        String productList = products.stream()
                .map(p -> {
                    String priceStr = p.getPrice() != null ? String.format("%.0f", p.getPrice()) : "n/a";
                    String extras = String.join(" ",
                            p.getModel() != null && !p.getModel().isBlank() ? "Model:" + p.getModel() : "",
                            p.getRam() != null && !p.getRam().isBlank() ? "RAM:" + p.getRam() : "",
                            p.getStorage() != null && !p.getStorage().isBlank() ? "Storage:" + p.getStorage() : ""
                    ).trim();
                    String reason = p.getAiReason() != null ? p.getAiReason() : "";
                    return String.format(
                            "Title:%s | Brand:%s | Price:%s | Score:%s | Fit:%s %s",
                            p.getTitle() != null ? p.getTitle() : "",
                            p.getBrand() != null ? p.getBrand() : "",
                            priceStr,
                            p.getAiScore() != null ? p.getAiScore() : "-",
                            reason,
                            extras
                    );
                })
                .collect(Collectors.joining("\n"));

        String prompt = """
User message:
""" + userMessage + """

Catalog lines (ONLY recommend and compare these — never invent others):
""" + productList + """

Help them choose: best options, comparisons when useful, closest alternatives if nothing is a perfect fit.
Highlight budget, specs, and usage in simple terms. If the match is weak, say so and suggest what to adjust.
""";

        return callOpenAI(prompt, AiCallType.ASSISTANT);
    }

    // ========================= OPENROUTER API CALL =========================

    private static String getStructuredSystemPrompt() {
        return """
You are SmartShop AI STRUCTURED ENGINE.

You ONLY generate valid JSON.

RULES:
- Output ONLY JSON (no explanation, no markdown)
- Never include text outside JSON
- Never invent products, IDs, brands, or prices
- Only use data provided in input
- If unsure → use null / omit values — never fake fields

The user message will ask for EITHER intent extraction (one JSON object: budget, category, usage, preferences)
OR product ranking (JSON array of up to 5 objects: productId, score 1-10, reason).
Follow that message exactly; never output both formats in one reply.
""";
    }

    private static String getAssistantSystemPrompt() {
        return """
You are SmartShop AI — an intelligent shopping assistant for an e-commerce store.

PRIMARY GOAL:
Help users choose the best products from the given catalog.

STRICT RULES:
- ONLY recommend specific products when they are explicitly provided in the input list
- NEVER invent or assume products
- NEVER mention years or external world knowledge
- NEVER say "as an AI"
- NEVER go outside store context

BEHAVIOR:
- Be natural like ChatGPT
- Be helpful, clear, and conversational
- Give comparisons when useful
- Suggest closest alternatives if perfect match is missing
- If no perfect match → still recommend best available options from what was given

PRODUCT GUIDANCE:
- Focus on budget, specs, usage
- Highlight differences between products
- Explain why something is good in simple terms

TWO TYPES OF USER QUERIES:
- (A) Catalog-based: If products exist in the input list, recommend TOP 3–5 best matches ONLY from that list.
- (B) Out-of-catalog / imaginary: If user asks for something not in the input list (or a made-up model),
  do NOT invent products or specs. Explain simply in real-world terms, then suggest the closest REAL
  alternatives from the provided catalog if possible.

CORE RESPONSE RULES:
- Never say “no results found”.
- Never force the user to refine their search. You may offer optional tips without pressure.
- Always give the solution first. Keep responses short, clear, and confident.

TONE:
Friendly, smart, helpful, not robotic
""";
    }

    private static String getSuggestSystemPrompt() {
        return """
You are a product suggestion filter.

TASK:
From the given product list, pick the most relevant 5 titles.

RULES:
- Only use exact product titles from list
- Do NOT modify or invent names
- Output comma-separated titles only
- No explanation, no extra text
""";
    }

    private String callOpenAI(String prompt, AiCallType type) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openRouterApiKey);

        double temperature = type == AiCallType.ASSISTANT ? 0.7 : 0.2;
        double presencePenalty = type == AiCallType.ASSISTANT ? 0.6 : 0.0;
        double frequencyPenalty = type == AiCallType.ASSISTANT ? 0.4 : 0.0;

        String systemContent = switch (type) {
            case STRUCTURED -> getStructuredSystemPrompt();
            case SUGGEST -> getSuggestSystemPrompt();
            case ASSISTANT -> getAssistantSystemPrompt();
        };

        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-4o-mini");
        body.put("temperature", temperature);
        body.put("top_p", 0.9);
        body.put("presence_penalty", presencePenalty);
        body.put("frequency_penalty", frequencyPenalty);
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemContent),
                Map.of("role", "user", "content", prompt)
        ));

        System.out.println("[SmartShop AI] OpenRouter call type=" + type
                + " temperature=" + temperature
                + " promptLength=" + prompt.length());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                log.info("Calling OpenRouter (attempt {}) type={}", attempt, type);

                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

                HttpStatus status = HttpStatus.resolve(response.getStatusCode().value());
                log.info("OpenRouter response status={}", status);

                if (!status.is2xxSuccessful() || response.getBody() == null) {
                    log.warn("Non-success OpenRouter response or empty body, status={}", status);
                    continue;
                }

                Map<String, Object> responseBody = response.getBody();

                Object choicesObj = responseBody.get("choices");
                if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
                    log.warn("OpenRouter response missing choices");
                    continue;
                }

                Object firstChoice = choices.get(0);
                if (!(firstChoice instanceof Map<?, ?> firstChoiceMap)) {
                    log.warn("OpenRouter first choice is not a map");
                    continue;
                }

                Object messageObj = firstChoiceMap.get("message");
                if (!(messageObj instanceof Map<?, ?> messageMap)) {
                    log.warn("OpenRouter message is not a map");
                    continue;
                }

                Object contentObj = messageMap.get("content");
                if (contentObj == null) {
                    log.warn("OpenRouter message content is null");
                    continue;
                }

                String content = contentObj.toString().trim();
                if (content.isEmpty()) {
                    log.warn("OpenRouter message content is empty");
                    continue;
                }

                log.info("OpenRouter response content length={}", content.length());
                System.out.println("[SmartShop AI] OpenRouter OK chars=" + content.length());
                return content;

            } catch (HttpClientErrorException.TooManyRequests e) {
                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                log.error("OpenRouter error on attempt {}: {}", attempt, e.getMessage());
            }
        }

        log.error("All OpenRouter attempts failed for prompt");
        return "Sorry, I couldn't fetch recommendations right now. Try again.";
    }

    // ========================= HELPERS =========================
    private List<AIProductRank> parseAIRanking(String jsonResponse) {
        try {
            // Clean response (remove markdown code blocks if any)
            String cleaned = jsonResponse
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            // Manual parsing for simplicity (or use Jackson)
            List<AIProductRank> results = new ArrayList<>();

            // Basic parsing - extract objects between { }
            String[] objects = cleaned.split("\\{");
            for (String obj : objects) {
                if (!obj.contains("productId")) continue;

                String id = extractValue(obj, "productId");
                String scoreStr = extractValue(obj, "score");
                String reason = extractValue(obj, "reason");

                if (id != null && scoreStr != null) {
                    results.add(new AIProductRank(
                            id.replaceAll("\"", "").trim(),
                            Integer.parseInt(scoreStr.trim()),
                            reason != null ? reason.replaceAll("\"", "").trim() : ""
                    ));
                }
            }

            return results;

        } catch (Exception e) {
            log.error("Failed to parse AI ranking: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String extractValue(String text, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"?([^\",\\}]*)\"?";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private List<String> splitToList(String result) {
        List<String> list = new ArrayList<>();
        if (result == null || result.isBlank()) return list;

        for (String s : result.split("[,\\n]")) {
            String cleaned = s.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
            if (!cleaned.isEmpty()) list.add(cleaned);
        }
        return list;
    }

    // ========================= INTERNAL CLASSES =========================
    private record AIProductRank(String productId, int score, String reason) {}
}