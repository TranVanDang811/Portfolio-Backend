package com.portfolioBackend.backend.service.impl;

import com.portfolioBackend.backend.PageView.dto.*;
import com.portfolioBackend.backend.PageView.entity.PageView;
import com.portfolioBackend.backend.PageView.repository.PageViewRepository;
import com.portfolioBackend.backend.service.PageViewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PageViewServiceImpl implements PageViewService {

    PageViewRepository pageViewRepository;

    // Record incoming page view visit with request metadata
    @Override
    @Transactional
    public PageViewResponse recordPageView(PageViewRequest request, String ipAddress, String userAgent, String referrer) {
        PageView pageView = PageView.builder()
                .sessionId(request.getSessionId())
                .page(request.getPage())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .referrer(referrer)
                .visitedAt(LocalDateTime.now())
                .build();

        PageView savedPageView = pageViewRepository.save(pageView);

        return PageViewResponse.builder()
                .id(savedPageView.getId())
                .page(savedPageView.getPage())
                .visitedAt(savedPageView.getVisitedAt())
                .build();
    }

    // Retrieve summarized view counts and unique visitors
    @Override
    @Transactional(readOnly = true)
    public PageViewSummaryResponse getSummaryStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();

        long totalViews = pageViewRepository.count();
        long todayViews = pageViewRepository.countByVisitedAtBetween(startOfToday, endOfToday);
        long thisMonthViews = pageViewRepository.countByVisitedAtBetween(startOfMonth, endOfToday);

        long totalUniqueVisitors = pageViewRepository.countDistinctSessionId();
        long todayUniqueVisitors = pageViewRepository.countDistinctSessionIdByVisitedAtBetween(startOfToday, endOfToday);

        return PageViewSummaryResponse.builder()
                .totalViews(totalViews)
                .todayViews(todayViews)
                .thisMonthViews(thisMonthViews)
                .totalUniqueVisitors(totalUniqueVisitors)
                .todayUniqueVisitors(todayUniqueVisitors)
                .build();
    }

    // Generate daily views and visitors timeline over a given number of days
    @Override
    @Transactional(readOnly = true)
    public List<PageViewTimelineResponse> getTimelineStats(int days) {
        if (days <= 0) days = 7;
        if (days > 90) days = 90;

        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        LocalDateTime startDateTime = startDate.atStartOfDay();

        List<Object[]> rawStats = pageViewRepository.getDailyStatsSince(startDateTime);

        Map<LocalDate, PageViewTimelineResponse> dateMap = new HashMap<>();
        for (Object[] row : rawStats) {
            LocalDate date;
            if (row[0] instanceof Date sqlDate) {
                date = sqlDate.toLocalDate();
            } else if (row[0] instanceof java.time.LocalDate localDate) {
                date = localDate;
            } else {
                date = LocalDate.parse(row[0].toString());
            }

            long views = ((Number) row[1]).longValue();
            long uniqueVisitors = ((Number) row[2]).longValue();

            dateMap.put(date, PageViewTimelineResponse.builder()
                    .date(date)
                    .views(views)
                    .uniqueVisitors(uniqueVisitors)
                    .build());
        }

        // Fill missing days with zero views in date range
        List<PageViewTimelineResponse> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate current = startDate.plusDays(i);
            PageViewTimelineResponse item = dateMap.getOrDefault(current, PageViewTimelineResponse.builder()
                    .date(current)
                    .views(0)
                    .uniqueVisitors(0)
                    .build());
            result.add(item);
        }

        return result;
    }

    // Get top visited page paths
    @Override
    @Transactional(readOnly = true)
    public List<PageViewTopPageResponse> getTopPages(int limit) {
        if (limit <= 0) limit = 5;

        List<Object[]> rows = pageViewRepository.findTopPages();
        return rows.stream()
                .limit(limit)
                .map(row -> PageViewTopPageResponse.builder()
                        .page((String) row[0])
                        .views(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    // Get total lifetime page views
    @Override
    @Transactional(readOnly = true)
    public long getTotalViews() {
        return pageViewRepository.count();
    }
}