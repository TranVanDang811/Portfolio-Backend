package com.portfolioBackend.backend.controller;

import com.portfolioBackend.backend.Project.dto.ProjectImageResponse;
import com.portfolioBackend.backend.service.ProjectImageService;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/project-images", "/project-images"})
@RequiredArgsConstructor
public class ProjectImageController {

    private final ProjectImageService projectImageService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/multiple")
    public ApiResponse<List<ProjectImageResponse>> uploadImages(
            @RequestParam("projectId") String projectId,
            @RequestParam("files") MultipartFile[] files) {
        return ApiResponse.<List<ProjectImageResponse>>builder()
                .result(projectImageService.uploadImages(projectId, files))
                .build();
    }

    @GetMapping("/by-project/{projectId}")
    public ApiResponse<List<ProjectImageResponse>> getImagesByProjectId(@PathVariable String projectId) {
        return ApiResponse.<List<ProjectImageResponse>>builder()
                .result(projectImageService.getImagesByProjectId(projectId))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{imageId}")
    public ApiResponse<Void> deleteImage(@PathVariable String imageId) {
        projectImageService.deleteImageById(imageId);
        return ApiResponse.<Void>builder().message("Delete image successfully").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ApiResponse<Void> deleteImages(@RequestBody List<String> imageIds) {
        projectImageService.deleteImagesByIds(imageIds);
        return ApiResponse.<Void>builder().message("Delete all image successfully").build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/set-main")
    public ApiResponse<ProjectImageResponse> setMainImage(@PathVariable String id) {
        return ApiResponse.<ProjectImageResponse>builder()
                .result(projectImageService.setMainImage(id))
                .build();
    }
}
