package com.portfolioBackend.backend.controller;

import com.portfolioBackend.backend.Testimonial.dto.TestimonialRequest;
import com.portfolioBackend.backend.Testimonial.dto.TestimonialResponse;
import com.portfolioBackend.backend.common.exception.ApiResponse;
import com.portfolioBackend.backend.service.TestimonialService;
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
public class TestimonialController {

    TestimonialService testimonialService;

    // Public API lấy danh sách testimonial
    @GetMapping("/testimonials")
    public ApiResponse<List<TestimonialResponse>> getAllTestimonials() {
        return ApiResponse.<List<TestimonialResponse>>builder()
                .result(testimonialService.getAll())
                .build();
    }

    // Public API lấy chi tiết 1 testimonial
    @GetMapping("/testimonials/{id}")
    public ApiResponse<TestimonialResponse> getTestimonialById(@PathVariable String id) {
        return ApiResponse.<TestimonialResponse>builder()
                .result(testimonialService.getById(id))
                .build();
    }

    // Admin API tạo testimonial
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping({"/admin/testimonials", "/testimonials"})
    public ApiResponse<TestimonialResponse> createTestimonial(@Valid @RequestBody TestimonialRequest request) {
        return ApiResponse.<TestimonialResponse>builder()
                .message("Create testimonial successfully")
                .result(testimonialService.create(request))
                .build();
    }

    // Admin API cập nhật testimonial
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping({"/admin/testimonials/{id}", "/testimonials/{id}"})
    public ApiResponse<TestimonialResponse> updateTestimonial(
            @PathVariable String id,
            @Valid @RequestBody TestimonialRequest request
    ) {
        return ApiResponse.<TestimonialResponse>builder()
                .message("Update testimonial successfully")
                .result(testimonialService.update(id, request))
                .build();
    }

    // Admin API xóa testimonial
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping({"/admin/testimonials/{id}", "/testimonials/{id}"})
    public ApiResponse<Void> deleteTestimonial(@PathVariable String id) {
        testimonialService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Delete testimonial successfully")
                .build();
    }
}

