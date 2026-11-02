package com.portfolioBackend.backend.Testimonial.mapper;

import com.portfolioBackend.backend.Testimonial.dto.TestimonialRequest;
import com.portfolioBackend.backend.Testimonial.dto.TestimonialResponse;
import com.portfolioBackend.backend.Testimonial.entity.Testimonial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TestimonialMapper {

    @Mapping(target = "id", ignore = true)
    Testimonial toEntity(TestimonialRequest request);

    TestimonialResponse toResponse(Testimonial entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(TestimonialRequest request, @MappingTarget Testimonial entity);
}

