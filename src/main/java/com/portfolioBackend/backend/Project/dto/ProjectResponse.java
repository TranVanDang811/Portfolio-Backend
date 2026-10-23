package com.portfolioBackend.backend.Project.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectResponse {


    String id;

    String title;

    String slug;

    String thumbnailUrl;

    String githubUrl;

    String demoUrl;

    Boolean featured;

    Long viewCount;

    String shortDescription;

    String description;

    String technologies;

    String categoryId;

    String categoryName;

    List<ProjectImageResponse> images;
}