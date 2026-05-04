package com.example.SmartShop.AI.Assistant.Service;

import com.example.SmartShop.AI.Assistant.Repository.SearchQueryRepository;
import com.example.SmartShop.AI.Assistant.Entity.SearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SearchQueryService {

    private final SearchQueryRepository searchQueryRepository;

    public SearchQueryService(SearchQueryRepository searchQueryRepository) {
        this.searchQueryRepository = searchQueryRepository;
    }

    public void log(String queryText, Double userBudget, UUID userId) {
        if (queryText == null || queryText.trim().isEmpty()) {
            return;
        }
        SearchQuery q = new SearchQuery();
        q.setQueryText(queryText.trim());
        q.setUserBudget(userBudget);
        q.setUserId(userId);
        searchQueryRepository.save(q);
    }

    public List<Map<String, Object>> getTrending(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        return searchQueryRepository.findTrendingQueries(PageRequest.of(0, safeLimit))
                .stream()
                .map(row -> Map.<String, Object>of(
                        "queryText", row[0],
                        "count", row[1]
                ))
                .toList();
    }
}
