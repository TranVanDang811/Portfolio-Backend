package com.portfolioBackend.backend.service;

import com.portfolioBackend.backend.Skill.dto.SkillRequest;
import com.portfolioBackend.backend.Skill.dto.SkillResponse;

import java.util.List;

public interface SkillService {

    SkillResponse create(SkillRequest request);

    SkillResponse update(String id, SkillRequest request);

    void delete(String id);

    List<SkillResponse> getAll();

    SkillResponse getById(String id);
}

