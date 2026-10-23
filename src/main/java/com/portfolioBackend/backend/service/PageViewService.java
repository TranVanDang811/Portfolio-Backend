package com.portfolioBackend.backend.service;

import com.portfolioBackend.backend.PageView.dto.PageViewRequest;
import com.portfolioBackend.backend.PageView.dto.PageViewResponse;
import com.portfolioBackend.backend.PageView.dto.PageViewSummaryResponse;
import com.portfolioBackend.backend.PageView.dto.PageViewTimelineResponse;
import com.portfolioBackend.backend.PageView.dto.PageViewTopPageResponse;

import java.util.List;

public interface PageViewService {
    PageViewResponse recordPageView(PageViewRequest request, String ipAddress, String userAgent, String referrer);

    PageViewSummaryResponse getSummaryStats();

    List<PageViewTimelineResponse> getTimelineStats(int days);

    List<PageViewTopPageResponse> getTopPages(int limit);

    long getTotalViews();
}
