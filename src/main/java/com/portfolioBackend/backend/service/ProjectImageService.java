package com.portfolioBackend.backend.service;

import com.portfolioBackend.backend.Project.dto.ProjectImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectImageService {
    List<ProjectImageResponse> uploadImages(String projectId, MultipartFile[] files);
    List<ProjectImageResponse> getImagesByProjectId(String projectId);
    void deleteImageById(String imageId);
    void deleteImagesByIds(List<String> imageIds);
    ProjectImageResponse setMainImage(String imageId);
}
