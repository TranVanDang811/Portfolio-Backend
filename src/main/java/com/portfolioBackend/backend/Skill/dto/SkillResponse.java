package com.portfolioBackend.backend.Skill.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkillResponse {

    String id;
    String name;
    String category;
    String icon;
    Integer displayOrder;
}

