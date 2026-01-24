package com.readflow.readflow_backend.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.readflow.readflow_backend.dto.admin.UploadResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final Cloudinary cloudinary;

    @Value("${spring.app.cloudinary.folder:readflow}")
    private String folder;

    public UploadResponse uploadImage(MultipartFile file) {
        validateImage(file);

        try {
            // Generate filename only (NO folder here)
            String publicId = UUID.randomUUID().toString();

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", folder, // ✅ correct
                            "public_id", publicId, // ✅ clean filename
                            "resource_type", "image",
                            "overwrite", true));

            return new UploadResponse(
                    result.get("secure_url").toString(),
                    result.get("public_id").toString() // readflow/uuid
            );

        } catch (Exception e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

    public void deleteImageByPublicId(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    Map.of("resource_type", "image"));
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary delete failed", e);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed");
        }

        long maxSize = 5L * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Image too large (max 5MB)");
        }
    }
}
