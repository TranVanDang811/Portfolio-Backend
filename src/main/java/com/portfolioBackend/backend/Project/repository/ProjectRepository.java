package com.portfolioBackend.backend.Project.repository;

import com.portfolioBackend.backend.Project.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {

    @Override
    @EntityGraph(attributePaths = {"category", "images"})
    List<Project> findAll();

    @Override
    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Project> findById(String id);

    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Project> findBySlug(String slug);

    @EntityGraph(attributePaths = {"category", "images"})
    List<Project> findByFeaturedTrue();

    @EntityGraph(attributePaths = {"category", "images"})
    List<Project> findByCategoryId(String categoryId);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("UPDATE Project p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") String id);
}
