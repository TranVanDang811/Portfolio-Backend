package com.portfolioBackend.backend.PageView.repository;

import com.portfolioBackend.backend.PageView.entity.PageView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, String> {

    long countByPage(String page);

    long countByVisitedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT p.sessionId) FROM PageView p")
    long countDistinctSessionId();

    @Query("SELECT COUNT(DISTINCT p.sessionId) FROM PageView p WHERE p.visitedAt BETWEEN :start AND :end")
    long countDistinctSessionIdByVisitedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT DATE(p.visited_at) as viewDate, " +
            "COUNT(p.id) as totalViews, " +
            "COUNT(DISTINCT p.session_id) as uniqueVisitors " +
            "FROM page_views p " +
            "WHERE p.visited_at >= :startDate " +
            "GROUP BY DATE(p.visited_at) " +
            "ORDER BY viewDate ASC", nativeQuery = true)
    List<Object[]> getDailyStatsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p.page, COUNT(p.id) FROM PageView p GROUP BY p.page ORDER BY COUNT(p.id) DESC")
    List<Object[]> findTopPages();
}