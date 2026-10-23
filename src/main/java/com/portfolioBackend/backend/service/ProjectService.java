package com.portfolioBackend.backend.service;

import com.portfolioBackend.backend.Project.dto.ProjectRequest;
import com.portfolioBackend.backend.Project.dto.ProjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    ProjectResponse create(
            ProjectRequest request,
            MultipartFile[] images
    );
    List<ProjectResponse> getAll();
    ProjectResponse getById(String projectId);
    ProjectResponse getBySlug(String slug);
    List<ProjectResponse> getFeaturedProjects();
    List<ProjectResponse> getProjectsByCategory(String categoryId);
    ProjectResponse update(
            String projectId,
            ProjectRequest request
    );
    void delete(String projectId);
}
