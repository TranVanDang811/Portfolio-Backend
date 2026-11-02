package com.portfolioBackend.backend.controller;

import com.portfolioBackend.backend.Skill.dto.SkillRequest;
import com.portfolioBackend.backend.Skill.dto.SkillResponse;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import com.portfolioBackend.backend.service.SkillService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillController {

    SkillService skillService;

    @GetMapping("/skills")
    public ApiResponse<List<SkillResponse>> getAllSkills() {
        return ApiResponse.<List<SkillResponse>>builder()
                .result(skillService.getAll())
                .build();
    }

    @GetMapping("/skills/{id}")
    public ApiResponse<SkillResponse> getSkillById(@PathVariable String id) {
        return ApiResponse.<SkillResponse>builder()
                .result(skillService.getById(id))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping({"/admin/skills", "/skills"})
    public ApiResponse<SkillResponse> createSkill(@Valid @RequestBody SkillRequest request) {
        return ApiResponse.<SkillResponse>builder()
                .message("Create skill successfully")
                .result(skillService.create(request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping({"/admin/skills/{id}", "/skills/{id}"})
    public ApiResponse<SkillResponse> updateSkill(
            @PathVariable String id,
            @Valid @RequestBody SkillRequest request) {
        return ApiResponse.<SkillResponse>builder()
                .message("Update skill successfully")
                .result(skillService.update(id, request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping({"/admin/skills/{id}", "/skills/{id}"})
    public ApiResponse<Void> deleteSkill(@PathVariable String id) {
        skillService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Delete skill successfully")
                .build();
    }
}

