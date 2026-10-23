package com.portfolioBackend.backend.service.impl;

import com.portfolioBackend.backend.AboutMe.dto.AboutMeRequest;
import com.portfolioBackend.backend.AboutMe.dto.AboutMeResponse;
import com.portfolioBackend.backend.AboutMe.entity.AboutMe;
import com.portfolioBackend.backend.AboutMe.mapper.AboutMeMapper;
import com.portfolioBackend.backend.AboutMe.repository.AboutMeRepository;
import com.portfolioBackend.backend.cloudinary.response.CloudinaryUploadResult;
import com.portfolioBackend.backend.cloudinary.service.CloudinaryService;
import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.service.AboutMeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AboutMeServiceImpl implements AboutMeService {
    AboutMeRepository aboutMeRepository;
    AboutMeMapper aboutMeMapper;
    CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public AboutMeResponse getAboutMe() {
        AboutMe aboutMe = aboutMeRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ABOUT_ME_NOT_FOUND));
        return aboutMeMapper.toResponse(aboutMe);
    }

    @Override
    public AboutMeResponse createAboutMe(AboutMeRequest request, MultipartFile avatar) {
        if (aboutMeRepository.count() > 0) {
            throw new AppException(ErrorCode.ABOUT_ME_ALREADY_EXISTS);
        }
        AboutMe aboutMe = aboutMeMapper.toEntity(request);
        if (avatar != null && !avatar.isEmpty()) {
            CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(avatar);
            aboutMe.setAvatarUrl(uploadResult.getUrl());
            aboutMe.setAvatarPublicId(uploadResult.getPublicId());
        }
        AboutMe savedAboutMe = aboutMeRepository.save(aboutMe);
        return aboutMeMapper.toResponse(savedAboutMe);
    }

    @Override
    public AboutMeResponse updateAboutMe(AboutMeRequest request, MultipartFile avatar) {
        AboutMe aboutMe = aboutMeRepository.findAll().stream().findFirst().orElseThrow(() -> new AppException(ErrorCode.ABOUT_ME_NOT_FOUND));
        aboutMeMapper.updateEntity(request, aboutMe);
        if (avatar != null && !avatar.isEmpty()) {
            String oldAvatarPublicId = aboutMe.getAvatarPublicId();
            // Upload new avatar first
            CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(avatar);
            aboutMe.setAvatarUrl(uploadResult.getUrl());
            aboutMe.setAvatarPublicId(uploadResult.getPublicId());
            // Delete old avatar after successful upload
            if (oldAvatarPublicId != null && !oldAvatarPublicId.isBlank()) {
                cloudinaryService.deleteImage(oldAvatarPublicId);
            }
        }
        AboutMe updatedAboutMe = aboutMeRepository.save(aboutMe);
        return aboutMeMapper.toResponse(updatedAboutMe);
    }
}
