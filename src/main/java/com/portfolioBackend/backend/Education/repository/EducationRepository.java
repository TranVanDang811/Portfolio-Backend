package com.portfolioBackend.backend.Education.repository;

import com.portfolioBackend.backend.Education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, String> {
    List<Education> findAllByOrderByDisplayOrderAsc();
}
