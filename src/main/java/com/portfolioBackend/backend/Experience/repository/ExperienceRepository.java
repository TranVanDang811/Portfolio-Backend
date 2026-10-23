package com.portfolioBackend.backend.Experience.repository;

import com.portfolioBackend.backend.Experience.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, String> {
    List<Experience> findAllByOrderByDisplayOrderAsc();
}