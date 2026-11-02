package com.portfolioBackend.backend.AboutMe.dto;

import jakarta.validation.constraints.Email;
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
public class AboutMeRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    String fullName;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    String title;

    String shortDescription;

    String description;


    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone;

    @Size(max = 150, message = "Location must not exceed 150 characters")
    String location;

    @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
    String githubUrl;

    @Size(max = 500, message = "LinkedIn URL must not exceed 500 characters")
    String linkedinUrl;

    @Size(max = 500, message = "Facebook URL must not exceed 500 characters")
    String facebookUrl;

    @Size(max = 500, message = "Instagram URL must not exceed 500 characters")
    String instagramUrl;

    @Size(max = 500, message = "CV URL must not exceed 500 characters")
    String cvUrl;
}
