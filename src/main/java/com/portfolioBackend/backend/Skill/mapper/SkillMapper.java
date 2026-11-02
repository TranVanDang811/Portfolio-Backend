package com.portfolioBackend.backend.Skill.mapper;

import com.portfolioBackend.backend.Skill.dto.SkillRequest;
import com.portfolioBackend.backend.Skill.dto.SkillResponse;
import com.portfolioBackend.backend.Skill.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(target = "id", ignore = true)
    Skill toEntity(SkillRequest request);

    SkillResponse toResponse(Skill entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(SkillRequest request, @MappingTarget Skill entity);
}
