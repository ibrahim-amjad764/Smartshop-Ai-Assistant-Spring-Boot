package com.example.SmartShop.AI.Assistant.Repository;

import com.example.SmartShop.AI.Assistant.Entity.SearchQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SearchQueryRepository extends JpaRepository<SearchQuery, UUID> {

    List<SearchQuery> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("""
            SELECT sq.queryText, COUNT(sq.id)
            FROM SearchQuery sq
            GROUP BY sq.queryText
            ORDER BY COUNT(sq.id) DESC
            """)
    List<Object[]> findTrendingQueries(Pageable pageable);
}
