package com.portfolioBackend.backend.service;

import com.portfolioBackend.backend.Testimonial.dto.TestimonialRequest;
import com.portfolioBackend.backend.Testimonial.dto.TestimonialResponse;

import java.util.List;

public interface TestimonialService {

    TestimonialResponse create(TestimonialRequest request);

    TestimonialResponse update(String id, TestimonialRequest request);

    void delete(String id);

    List<TestimonialResponse> getAll();

    TestimonialResponse getById(String id);
}

