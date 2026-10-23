package com.portfolioBackend.backend.ProjectCategory.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectCategoryResponse {

    UUID id;

    String name;

    String description;
}