package com.portfolioBackend.backend.ProjectCategory.repository;

import com.portfolioBackend.backend.ProjectCategory.entity.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, String> {

    Optional<ProjectCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}