package com.readflow.readflow_backend.service;

import java.io.IOException;
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
            // store folder/publicId so we can delete later reliably
            String publicId = folder + "/" + UUID.randomUUID();

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", true));

            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new RuntimeException("Upload failed: secure_url missing");
            }

            return new UploadResponse(secureUrl.toString(), publicId);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read upload file", e);
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

    public void deleteImageByPublicId(String publicId) {
        if (publicId == null || publicId.isBlank())
            return;

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

        long max = 5L * 1024 * 1024; // 5MB
        if (file.getSize() > max) {
            throw new IllegalArgumentException("Image too large (max 5MB)");
        }
    }
}
