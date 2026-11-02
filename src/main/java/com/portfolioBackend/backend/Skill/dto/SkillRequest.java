package com.portfolioBackend.backend.Skill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name must not exceed 100 characters")
    String name;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    String category;

    @Size(max = 500, message = "Icon URL or icon class must not exceed 500 characters")
    String icon;

    Integer displayOrder;
}

