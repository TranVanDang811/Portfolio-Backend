package com.portfolioBackend.backend.cloudinary.service;

import com.portfolioBackend.backend.common.exception.AppException;
import com.portfolioBackend.backend.common.exception.ErrorCode;
import com.portfolioBackend.backend.cloudinary.response.CloudinaryUploadResult;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    // Upload image to Cloudinary and return URL with public ID
    public CloudinaryUploadResult uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            return new CloudinaryUploadResult(url, publicId);
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }

    // Delete image from Cloudinary by public ID
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
    }
}
