package com.portfolioBackend.backend.Testimonial.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestimonialResponse {

    String id;
    String name;
    String role;
    String review;
    String avatarUrl;
    Integer displayOrder;
}

