package com.portfolioBackend.backend.Project.dto;

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
public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    String title;

    String slug;

    @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
    String githubUrl;

    @Size(max = 500, message = "Demo URL must not exceed 500 characters")
    String demoUrl;

    Boolean featured;

    @Size(max = 500, message = "Short description must not exceed 500 characters")
    String shortDescription;

    String description;

    String technologies;

    @NotBlank(message = "Category ID is required")
    String categoryId;
}
