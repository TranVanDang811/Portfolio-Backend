package com.portfolioBackend.backend.Skill.repository;

import com.portfolioBackend.backend.Skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, String> {
    List<Skill> findAllByOrderByDisplayOrderAsc();
    boolean existsByNameIgnoreCase(String name);
}
