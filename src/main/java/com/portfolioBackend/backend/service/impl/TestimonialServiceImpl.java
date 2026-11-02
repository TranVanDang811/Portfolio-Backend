package com.portfolioBackend.backend.service.impl;

import com.portfolioBackend.backend.Testimonial.dto.TestimonialRequest;
import com.portfolioBackend.backend.Testimonial.dto.TestimonialResponse;
import com.portfolioBackend.backend.Testimonial.entity.Testimonial;
import com.portfolioBackend.backend.Testimonial.mapper.TestimonialMapper;
import com.portfolioBackend.backend.Testimonial.repository.TestimonialRepository;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.TestimonialService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class TestimonialServiceImpl implements TestimonialService {

    TestimonialRepository testimonialRepository;
    TestimonialMapper testimonialMapper;

    @Override
    public TestimonialResponse create(TestimonialRequest request) {
        Testimonial testimonial = testimonialMapper.toEntity(request);
        if (testimonial.getDisplayOrder() == null) {
            testimonial.setDisplayOrder(0);
        }

        Testimonial saved = testimonialRepository.save(testimonial);
        return testimonialMapper.toResponse(saved);
    }

    @Override
    public TestimonialResponse update(String id, TestimonialRequest request) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TESTIMONIAL_NOT_FOUND));

        testimonialMapper.updateEntity(request, testimonial);
        Testimonial updated = testimonialRepository.save(testimonial);
        return testimonialMapper.toResponse(updated);
    }

    @Override
    public void delete(String id) {
        if (!testimonialRepository.existsById(id)) {
            throw new AppException(ErrorCode.TESTIMONIAL_NOT_FOUND);
        }
        testimonialRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestimonialResponse> getAll() {
        return testimonialRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(testimonialMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TestimonialResponse getById(String id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TESTIMONIAL_NOT_FOUND));
        return testimonialMapper.toResponse(testimonial);
    }
}

