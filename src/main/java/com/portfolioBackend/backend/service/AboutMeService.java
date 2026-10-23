package com.portfolioBackend.backend.service;

import com.portfolioBackend.backend.AboutMe.dto.AboutMeRequest;
import com.portfolioBackend.backend.AboutMe.dto.AboutMeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AboutMeService {

    AboutMeResponse getAboutMe();

    AboutMeResponse createAboutMe(AboutMeRequest request, MultipartFile avatar);

    AboutMeResponse updateAboutMe(AboutMeRequest request, MultipartFile avatar);
}