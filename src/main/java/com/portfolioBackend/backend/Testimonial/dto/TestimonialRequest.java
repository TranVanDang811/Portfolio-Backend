package com.portfolioBackend.backend.Testimonial.dto;

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
public class TestimonialRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    String name;

    @NotBlank(message = "Role is required")
    @Size(max = 200, message = "Role must not exceed 200 characters")
    String role;

    @NotBlank(message = "Review is required")
    String review;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    String avatarUrl;

    Integer displayOrder;
}

