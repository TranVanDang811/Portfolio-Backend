package com.portfolioBackend.backend.service.impl;

import com.portfolioBackend.backend.Skill.dto.SkillRequest;
import com.portfolioBackend.backend.Skill.dto.SkillResponse;
import com.portfolioBackend.backend.Skill.entity.Skill;
import com.portfolioBackend.backend.Skill.mapper.SkillMapper;
import com.portfolioBackend.backend.Skill.repository.SkillRepository;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.SkillService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class SkillServiceImpl implements SkillService {

    SkillRepository skillRepository;
    SkillMapper skillMapper;

    @Override
    public SkillResponse create(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.SKILL_ALREADY_EXISTS);
        }

        Skill skill = skillMapper.toEntity(request);
        if (skill.getDisplayOrder() == null) {
            skill.setDisplayOrder(0);
        }

        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toResponse(savedSkill);
    }

    @Override
    public SkillResponse update(String id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND));

        if (!skill.getName().equalsIgnoreCase(request.getName())
                && skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.SKILL_ALREADY_EXISTS);
        }

        skillMapper.updateEntity(request, skill);
        if (skill.getDisplayOrder() == null) {
            skill.setDisplayOrder(0);
        }

        Skill updatedSkill = skillRepository.save(skill);
        return skillMapper.toResponse(updatedSkill);
    }

    @Override
    public void delete(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND));

        skillRepository.delete(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAll() {
        return skillRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getById(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_FOUND));
        return skillMapper.toResponse(skill);
    }
}

