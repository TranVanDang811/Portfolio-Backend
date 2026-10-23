package com.portfolioBackend.backend.controller;

import com.portfolioBackend.backend.Education.entity.Education;
import com.portfolioBackend.backend.Education.repository.EducationRepository;
import com.portfolioBackend.backend.Experience.entity.Experience;
import com.portfolioBackend.backend.Experience.repository.ExperienceRepository;
import com.portfolioBackend.backend.Skill.entity.Skill;
import com.portfolioBackend.backend.Skill.repository.SkillRepository;
import com.portfolioBackend.backend.Testimonial.entity.Testimonial;
import com.portfolioBackend.backend.Testimonial.repository.TestimonialRepository;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PortfolioContentController {

    SkillRepository skillRepository;
    ExperienceRepository experienceRepository;
    EducationRepository educationRepository;
    TestimonialRepository testimonialRepository;

    @GetMapping("/skills")
    public ApiResponse<List<Skill>> getSkills() {
        return ApiResponse.<List<Skill>>builder()
                .result(skillRepository.findAllByOrderByDisplayOrderAsc())
                .build();
    }

    @GetMapping("/experiences")
    public ApiResponse<List<Experience>> getExperiences() {
        return ApiResponse.<List<Experience>>builder()
                .result(experienceRepository.findAllByOrderByDisplayOrderAsc())
                .build();
    }

    @GetMapping("/educations")
    public ApiResponse<List<Education>> getEducations() {
        return ApiResponse.<List<Education>>builder()
                .result(educationRepository.findAllByOrderByDisplayOrderAsc())
                .build();
    }

    @GetMapping("/testimonials")
    public ApiResponse<List<Testimonial>> getTestimonials() {
        return ApiResponse.<List<Testimonial>>builder()
                .result(testimonialRepository.findAllByOrderByDisplayOrderAsc())
                .build();
    }
}
