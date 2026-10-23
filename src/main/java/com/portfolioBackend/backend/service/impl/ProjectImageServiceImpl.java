package com.portfolioBackend.backend.service.impl;

import com.portfolioBackend.backend.Project.dto.ProjectImageResponse;
import com.portfolioBackend.backend.Project.entity.Project;
import com.portfolioBackend.backend.Project.entity.ProjectImage;
import com.portfolioBackend.backend.Project.mapper.ProjectMapper;
import com.portfolioBackend.backend.Project.repository.ProjectImageRepository;
import com.portfolioBackend.backend.Project.repository.ProjectRepository;
import com.portfolioBackend.backend.cloudinary.response.CloudinaryUploadResult;
import com.portfolioBackend.backend.cloudinary.service.CloudinaryService;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.ProjectImageService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectImageServiceImpl implements ProjectImageService {
    ProjectImageRepository imageRepository;
    ProjectRepository projectRepository;
    CloudinaryService cloudinaryService;
    ProjectMapper projectMapper;

    @Override
    @Transactional
    public List<ProjectImageResponse> uploadImages(String projectId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
        // Get the current maximum display order of this project
        int displayOrder = imageRepository.findMaxDisplayOrderByProjectId(projectId);
        List<ProjectImage> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            CloudinaryUploadResult result =
                    cloudinaryService.uploadImage(file);
            if (result.getUrl() != null && !result.getUrl().isBlank()) {
                ProjectImage image = ProjectImage.builder()
                        .imageUrl(result.getUrl())
                        .publicId(result.getPublicId())
                        .displayOrder(++displayOrder)
                        .project(project)
                        .build();
                images.add(image);
            }
        }
        if (images.isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
        return imageRepository.saveAll(images)
                .stream()
                .map(projectMapper::toProjectImageResponse)
                .toList();
    }

    @Override
    public List<ProjectImageResponse> getImagesByProjectId(
            String projectId) {
        List<ProjectImage> images =
                imageRepository.findByProjectIdOrderByDisplayOrderAsc(projectId);
        return images.stream()
                .map(projectMapper::toProjectImageResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteImageById(String imageId) {
        ProjectImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        Project project = image.getProject();
        // Prevent deleting the current project thumbnail
        if (project.getThumbnailUrl() != null && project.getThumbnailUrl().equals(image.getImageUrl())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_THUMBNAIL);
        }
        // Delete image from Cloudinary
        if (image.getPublicId() != null && !image.getPublicId().isBlank()) {
            cloudinaryService.deleteImage(image.getPublicId());
        }
        imageRepository.delete(image);
    }

    @Override
    @Transactional
    public void deleteImagesByIds(List<String> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        List<ProjectImage> images = imageRepository.findAllById(imageIds);
        if (images.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        } // Prevent deleting the current thumbnail
        for (ProjectImage image : images) {
            Project project = image.getProject();
            if (project.getThumbnailUrl() != null && project.getThumbnailUrl().equals(image.getImageUrl())) {
                throw new AppException(ErrorCode.CANNOT_DELETE_THUMBNAIL);
            }
        } // Delete images from Cloudinary
        for (ProjectImage image : images) {
            if (image.getPublicId() != null && !image.getPublicId().isBlank()) {
                cloudinaryService.deleteImage(image.getPublicId());
            }
        }
        imageRepository.deleteAll(images);
    }

    @Override
    @Transactional
    public ProjectImageResponse setMainImage(String imageId) {
        ProjectImage image = imageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        Project project = image.getProject(); // Set this image as the project's thumbnail
        project.setThumbnailUrl(image.getImageUrl());
        projectRepository.save(project);
        return projectMapper.toProjectImageResponse(image);
    }
}