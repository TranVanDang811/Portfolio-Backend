package com.portfolioBackend.backend.AboutMe.mapper;

import com.portfolioBackend.backend.AboutMe.dto.AboutMeRequest;
import com.portfolioBackend.backend.AboutMe.dto.AboutMeResponse;
import com.portfolioBackend.backend.AboutMe.entity.AboutMe;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AboutMeMapper {

    AboutMeResponse toResponse(AboutMe entity);

    AboutMe toEntity(AboutMeRequest request);

    void updateEntity(
            AboutMeRequest request,
            @MappingTarget AboutMe entity
    );
}