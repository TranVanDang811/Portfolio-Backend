package com.portfolioBackend.backend.Project.mapper;

import com.portfolioBackend.backend.Project.dto.ProjectImageResponse;
import com.portfolioBackend.backend.Project.dto.ProjectRequest;
import com.portfolioBackend.backend.Project.dto.ProjectResponse;
import com.portfolioBackend.backend.Project.entity.Project;
import com.portfolioBackend.backend.Project.entity.ProjectImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    Project toProject(ProjectRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProjectResponse toProjectResponse(Project project);

    ProjectImageResponse toProjectImageResponse(ProjectImage image);

    List<ProjectImageResponse> toProjectImageResponses(List<ProjectImage> images);

    void updateProject(
            ProjectRequest request,
            @MappingTarget Project project
    );
}
