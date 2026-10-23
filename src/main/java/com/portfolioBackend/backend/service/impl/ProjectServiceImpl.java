package com.portfolioBackend.backend.service.impl;

import com.portfolioBackend.backend.Project.dto.ProjectRequest;
import com.portfolioBackend.backend.Project.dto.ProjectResponse;
import com.portfolioBackend.backend.Project.entity.Project;
import com.portfolioBackend.backend.Project.entity.ProjectImage;
import com.portfolioBackend.backend.Project.repository.ProjectImageRepository;
import com.portfolioBackend.backend.Project.repository.ProjectRepository;
import com.portfolioBackend.backend.ProjectCategory.entity.ProjectCategory;
import com.portfolioBackend.backend.ProjectCategory.repository.ProjectCategoryRepository;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.ProjectService;
import com.portfolioBackend.backend.cloudinary.response.CloudinaryUploadResult;
import com.portfolioBackend.backend.cloudinary.service.CloudinaryService;
import com.portfolioBackend.backend.Project.mapper.ProjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;

    ProjectImageRepository imageRepository;

    ProjectCategoryRepository projectCategoryRepository;

    CloudinaryService cloudinaryService;

    ProjectMapper projectMapper;


    @Override
    public ProjectResponse create(ProjectRequest request, MultipartFile[] images) {
        Project project = projectMapper.toProject(request);
        ProjectCategory category = projectCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_CATEGORY_NOT_FOUND));
        project.setCategory(category);

        String slug = generateUniqueSlug(request.getTitle(), request.getSlug());
        project.setSlug(slug);

        List<ProjectImage> projectImages = new ArrayList<>();
        String thumbnail = null;
        int displayOrder = 1;
        if (images != null && images.length > 0) {
            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(file);
                ProjectImage image = ProjectImage.builder()
                        .imageUrl(uploadResult.getUrl())
                        .publicId(uploadResult.getPublicId())
                        .displayOrder(displayOrder++)
                        .project(project)
                        .build();
                projectImages.add(image);
                if (thumbnail == null) {
                    thumbnail = uploadResult.getUrl();
                }
            }
        }
        project.setThumbnailUrl(thumbnail);
        project.setImages(projectImages);
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        return projectRepository.findAll().stream().map(projectMapper::toProjectResponse).toList();
    }

    @Override
    public ProjectResponse getById(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
        projectRepository.incrementViewCount(projectId);
        project.setViewCount(project.getViewCount() + 1);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse getBySlug(String slug) {
        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
        projectRepository.incrementViewCount(project.getId());
        project.setViewCount(project.getViewCount() + 1);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getFeaturedProjects() {
        return projectRepository.findByFeaturedTrue().stream()
                .map(projectMapper::toProjectResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByCategory(String categoryId) {
        return projectRepository.findByCategoryId(categoryId).stream()
                .map(projectMapper::toProjectResponse)
                .toList();
    }


    @Override
    public ProjectResponse update(String projectId, ProjectRequest request) {

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        projectMapper.updateProject(request, project);

        if (request.getCategoryId() != null) {

            ProjectCategory category = projectCategoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.PROJECT_CATEGORY_NOT_FOUND));

            project.setCategory(category);
        }

        return projectMapper.toProjectResponse(projectRepository.save(project));
    }


    @Override
    public void delete(String projectId) {

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        for (ProjectImage image : project.getImages()) {

            if (image.getPublicId() != null) {
                cloudinaryService.deleteImage(image.getPublicId());
            }
        }

        imageRepository.deleteAll(project.getImages());

        projectRepository.delete(project);
    }

    private String generateUniqueSlug(String title, String customSlug) {
        String baseSlug = (customSlug != null && !customSlug.isBlank())
                ? toSlug(customSlug)
                : toSlug(title != null ? title : "project");
        String slug = baseSlug;
        int counter = 1;
        while (projectRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private String toSlug(String input) {
        if (input == null) return "project";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return slug.isBlank() ? "project" : slug;
    }
}