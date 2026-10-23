package com.portfolioBackend.backend.PageView.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageViewRequest {
    @NotBlank(message = "Session ID is required")
    @Size(max = 100, message = "Session ID must not exceed 100 characters")
    String sessionId;
    @NotBlank(message = "Page is required")
    @Size(max = 255, message = "Page must not exceed 255 characters")
    String page;
}
