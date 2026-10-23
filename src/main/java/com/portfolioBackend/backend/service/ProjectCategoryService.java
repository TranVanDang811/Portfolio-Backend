package com.portfolioBackend.backend.service;

import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryRequest;
import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryResponse;
import org.springframework.data.domain.Page;


public interface ProjectCategoryService {

    ProjectCategoryResponse create(ProjectCategoryRequest request);

    Page<ProjectCategoryResponse> getAll(int page, int size) ;

    ProjectCategoryResponse getById(String id);

    ProjectCategoryResponse update(String id, ProjectCategoryRequest request);

    void delete(String id);
}
