package com.portfolioBackend.backend.PageView.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageViewTimelineResponse {
    LocalDate date;
    long views;
    long uniqueVisitors;
}
