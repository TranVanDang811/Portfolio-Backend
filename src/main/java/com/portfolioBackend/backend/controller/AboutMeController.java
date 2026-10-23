package com.portfolioBackend.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolioBackend.backend.AboutMe.dto.AboutMeRequest;
import com.portfolioBackend.backend.AboutMe.dto.AboutMeResponse;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.AboutMeService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/about-me")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AboutMeController {

    AboutMeService aboutMeService;
    ObjectMapper objectMapper;
    Validator validator;

    @GetMapping
    public ApiResponse<AboutMeResponse> getAboutMe() {
        return ApiResponse.<AboutMeResponse>builder().result(aboutMeService.getAboutMe()).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AboutMeResponse> createAboutMe(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        AboutMeRequest request;
        try {
            request = objectMapper.readValue(dataJson, AboutMeRequest.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON);
        }

        Set<ConstraintViolation<AboutMeRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String firstError = violations.iterator().next().getMessage();
            throw new AppException(ErrorCode.INVALID_KEY, firstError);
        }

        return ApiResponse.<AboutMeResponse>builder().result(aboutMeService.createAboutMe(request, avatar)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AboutMeResponse> updateAboutMe(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        AboutMeRequest request;
        try {
            request = objectMapper.readValue(dataJson, AboutMeRequest.class);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_JSON);
        }

        Set<ConstraintViolation<AboutMeRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String firstError = violations.iterator().next().getMessage();
            throw new AppException(ErrorCode.INVALID_KEY, firstError);
        }

        return ApiResponse.<AboutMeResponse>builder().result(aboutMeService.updateAboutMe(request, avatar)).build();
    }
}