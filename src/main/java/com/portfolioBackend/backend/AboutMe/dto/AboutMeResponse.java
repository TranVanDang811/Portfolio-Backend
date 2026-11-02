package com.portfolioBackend.backend.AboutMe.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AboutMeResponse {

    UUID id;

    String fullName;

    String title;

    String shortDescription;

    String description;

    String avatarUrl;

    String email;

    String phone;

    String location;

    String githubUrl;

    String linkedinUrl;

    String facebookUrl;

    String instagramUrl;

    String cvUrl;
}
