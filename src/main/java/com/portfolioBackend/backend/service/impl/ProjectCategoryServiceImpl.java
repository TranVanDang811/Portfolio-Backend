package com.portfolioBackend.backend.service.impl;

import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryRequest;
import com.portfolioBackend.backend.ProjectCategory.dto.ProjectCategoryResponse;
import com.portfolioBackend.backend.ProjectCategory.entity.ProjectCategory;
import com.portfolioBackend.backend.ProjectCategory.mapper.ProjectCategoryMapper;
import com.portfolioBackend.backend.ProjectCategory.repository.ProjectCategoryRepository;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.ProjectCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectCategoryServiceImpl implements ProjectCategoryService {

    ProjectCategoryRepository projectCategoryRepository;
    ProjectCategoryMapper projectCategoryMapper;

    @Override
    public ProjectCategoryResponse create(ProjectCategoryRequest request) {

        if (projectCategoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.PROJECT_CATEGORY_ALREADY_EXISTS);
        }

        ProjectCategory category = projectCategoryMapper.toEntity(request);

        ProjectCategory savedCategory = projectCategoryRepository.save(category);

        return projectCategoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectCategoryResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return projectCategoryRepository.findAll(pageable).map(projectCategoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectCategoryResponse getById(String id) {

        ProjectCategory category = projectCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_CATEGORY_NOT_FOUND));

        return projectCategoryMapper.toResponse(category);
    }

    @Override
    public ProjectCategoryResponse update(
            String id,
            ProjectCategoryRequest request) {

        ProjectCategory category = projectCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_CATEGORY_NOT_FOUND));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && projectCategoryRepository.existsByNameIgnoreCase(request.getName())) {

            throw new AppException(ErrorCode.PROJECT_CATEGORY_ALREADY_EXISTS);
        }

        projectCategoryMapper.updateEntity(request, category);

        ProjectCategory updatedCategory = projectCategoryRepository.save(category);

        return projectCategoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void delete(String id) {

        ProjectCategory category = projectCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_CATEGORY_NOT_FOUND));

        projectCategoryRepository.delete(category);
    }
}