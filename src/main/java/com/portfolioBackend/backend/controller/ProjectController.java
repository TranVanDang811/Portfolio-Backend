package com.portfolioBackend.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolioBackend.backend.Project.dto.ProjectRequest;
import com.portfolioBackend.backend.Project.dto.ProjectResponse;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.ProjectService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectController {

    ProjectService projectService;
    ObjectMapper objectMapper;
    Validator validator;

    @GetMapping("/projects")
    public ApiResponse<List<ProjectResponse>> getAllProjects() {
        return ApiResponse.<List<ProjectResponse>>builder()
                .result(projectService.getAll())
                .build();
    }

    @GetMapping("/projects/{projectId}")
    public ApiResponse<ProjectResponse> getProject(@PathVariable String projectId) {
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.getById(projectId))
                .build();
    }

    @GetMapping("/projects/slug/{slug}")
    public ApiResponse<ProjectResponse> getProjectBySlug(@PathVariable String slug) {
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.getBySlug(slug))
                .build();
    }

    @GetMapping("/projects/featured")
    public ApiResponse<List<ProjectResponse>> getFeaturedProjects() {
        return ApiResponse.<List<ProjectResponse>>builder()
                .result(projectService.getFeaturedProjects())
                .build();
    }

    @GetMapping("/projects/by-category/{categoryId}")
    public ApiResponse<List<ProjectResponse>> getProjectsByCategory(@PathVariable String categoryId) {
        return ApiResponse.<List<ProjectResponse>>builder()
                .result(projectService.getProjectsByCategory(categoryId))
                .build();
    }

    // Admin endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/admin/projects", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<ProjectResponse> createProject(
            @RequestPart("project") String projectJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        ProjectRequest request;
        try {
            request = objectMapper.readValue(projectJson, ProjectRequest.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON);
        }

        Set<ConstraintViolation<ProjectRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String firstError = violations.iterator().next().getMessage();
            throw new AppException(ErrorCode.INVALID_KEY, firstError);
        }

        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.create(request, images))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping({"/admin/projects/{projectId}", "/admin/{projectId}"})
    public ApiResponse<ProjectResponse> updateProject(
            @PathVariable String projectId,
            @Valid @RequestBody ProjectRequest request
    ) {
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.update(projectId, request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping({"/admin/projects/{projectId}", "/admin/{projectId}"})
    public ApiResponse<Void> deleteProject(@PathVariable String projectId) {
        projectService.delete(projectId);
        return ApiResponse.<Void>builder()
                .message("Delete project successfully")
                .build();
    }
}