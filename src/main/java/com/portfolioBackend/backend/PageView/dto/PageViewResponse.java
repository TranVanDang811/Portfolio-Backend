package com.portfolioBackend.backend.PageView.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageViewResponse {
    String id;
    String page;
    LocalDateTime visitedAt;
}
