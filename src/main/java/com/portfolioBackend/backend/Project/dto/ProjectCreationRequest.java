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
public class ProjectCreationRequest {

    String title;

    String slug;

    String thumbnail;

    String githubUrl;

    String demoUrl;

    Boolean featured;

    String shortDescription;

    String description;

    String technologies;

    List<String> images;
}