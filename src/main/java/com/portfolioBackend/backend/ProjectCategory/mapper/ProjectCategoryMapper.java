package com.portfolioBackend.backend.ProjectCategory.mapper;


import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryRequest;
import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryResponse;
import com.portfolioBackend.backend.ProjectCategory.entity.ProjectCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectCategoryMapper {

    ProjectCategory toEntity(ProjectCategoryRequest request);

    ProjectCategoryResponse toResponse(ProjectCategory entity);

    void updateEntity(
            ProjectCategoryRequest request,
            @MappingTarget ProjectCategory entity
    );
}