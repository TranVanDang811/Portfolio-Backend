package com.portfolioBackend.backend.controller;

import com.portfolioBackend.backend.PageView.dto.PageViewRequest;
import com.portfolioBackend.backend.PageView.dto.PageViewResponse;
import com.portfolioBackend.backend.PageView.dto.PageViewSummaryResponse;
import com.portfolioBackend.backend.PageView.dto.PageViewTimelineResponse;
import com.portfolioBackend.backend.PageView.dto.PageViewTopPageResponse;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import com.portfolioBackend.backend.service.PageViewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PageViewController {

    PageViewService pageViewService;

    /**
     * Public API: Ghi nhận 1 lượt truy cập trang
     */
    @PostMapping("/api/page-views")
    public ApiResponse<PageViewResponse> recordPageView(@Valid @RequestBody PageViewRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String referrer = httpRequest.getHeader("Referer");
        return ApiResponse.<PageViewResponse>builder()
                .result(pageViewService.recordPageView(request, ipAddress, userAgent, referrer))
                .build();
    }

    /**
     * Public API: Lấy tổng số lượt xem công khai (ví dụ hiển thị widget footer)
     */
    @GetMapping("/api/page-views/count")
    public ApiResponse<Map<String, Long>> getTotalViews() {
        return ApiResponse.<Map<String, Long>>builder()
                .result(Map.of("totalViews", pageViewService.getTotalViews()))
                .build();
    }

    /**
     * Admin API: Thống kê tổng quan (Hôm nay, Tháng này, Tổng, Khách duy nhất)
     */
    @GetMapping("/api/admin/page-views/stats/summary")
    public ApiResponse<PageViewSummaryResponse> getSummaryStats() {
        return ApiResponse.<PageViewSummaryResponse>builder()
                .result(pageViewService.getSummaryStats())
                .build();
    }

    /**
     * Admin API: Thống kê số view theo từng ngày (Timeline cho biểu đồ)
     * Mặc định 7 ngày, tối đa 90 ngày
     */
    @GetMapping("/api/admin/page-views/stats/timeline")
    public ApiResponse<List<PageViewTimelineResponse>> getTimelineStats(
            @RequestParam(defaultValue = "7") int days
    ) {
        return ApiResponse.<List<PageViewTimelineResponse>>builder()
                .result(pageViewService.getTimelineStats(days))
                .build();
    }

    /**
     * Admin API: Top các trang được truy cập nhiều nhất
     */
    @GetMapping("/api/admin/page-views/stats/top-pages")
    public ApiResponse<List<PageViewTopPageResponse>> getTopPages(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ApiResponse.<List<PageViewTopPageResponse>>builder()
                .result(pageViewService.getTopPages(limit))
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
