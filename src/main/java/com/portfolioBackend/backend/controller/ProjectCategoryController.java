package com.portfolioBackend.backend.controller;

import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryRequest;
import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryResponse;
import com.portfolioBackend.backend.service.ProjectCategoryService;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectCategoryController {

    private final ProjectCategoryService projectCategoryService;

    @GetMapping("/project-categories")
    public ApiResponse<Page<ProjectCategoryResponse>> getAll(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "5") int size) {

        return ApiResponse.<Page<ProjectCategoryResponse>>builder()
                .result(projectCategoryService.getAll(page - 1, size))
                .build();
    }

    @GetMapping("/project-categories/{id}")
    public ApiResponse<ProjectCategoryResponse> getById(
            @PathVariable String id) {

        return ApiResponse.<ProjectCategoryResponse>builder()
                .result(projectCategoryService.getById(id))
                .build();
    }

    // Admin endpoints
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/project-categories")
    public ApiResponse<ProjectCategoryResponse> create(
            @Valid @RequestBody ProjectCategoryRequest request) {

        return ApiResponse.<ProjectCategoryResponse>builder()
                .result(projectCategoryService.create(request))
                .build();
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/project-categories/{id}")
    public ApiResponse<ProjectCategoryResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ProjectCategoryRequest request) {

        return ApiResponse.<ProjectCategoryResponse>builder()
                .result(projectCategoryService.update(id, request))
                .build();
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/project-categories/{id}")
    public ApiResponse<Void> delete(
            @PathVariable String id) {

        projectCategoryService.delete(id);

        return ApiResponse.<Void>builder()
                .message("Delete project category successfully")
                .build();
    }
}