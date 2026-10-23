package com.portfolioBackend.backend.PageView.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageViewSummaryResponse {
    long totalViews;
    long todayViews;
    long thisMonthViews;
    long totalUniqueVisitors;
    long todayUniqueVisitors;
}
