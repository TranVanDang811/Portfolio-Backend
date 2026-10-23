package com.portfolioBackend.backend.Project.repository;

import com.portfolioBackend.backend.Project.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, String> {
    List<ProjectImage> findByProjectIdOrderByDisplayOrderAsc(String projectId);

    @Query(""" 
            SELECT COALESCE(MAX(pi.displayOrder), 0) FROM ProjectImage pi WHERE pi.project.id = :projectId 
            """)
    int findMaxDisplayOrderByProjectId(@Param("projectId") String projectId);
}