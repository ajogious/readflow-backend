package com.readflow.readflow_backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.admin.AssignCategoriesRequest;
import com.readflow.readflow_backend.dto.admin.ContentResponse;
import com.readflow.readflow_backend.dto.admin.CreateContentRequest;
import com.readflow.readflow_backend.dto.admin.UpdateContentRequest;
import com.readflow.readflow_backend.dto.content.CategorySummary;
import com.readflow.readflow_backend.entity.Category;
import com.readflow.readflow_backend.entity.Content;
import com.readflow.readflow_backend.entity.ContentStatus;
import com.readflow.readflow_backend.repository.CategoryRepository;
import com.readflow.readflow_backend.repository.ContentRepository;
import com.readflow.readflow_backend.repository.UserRepository;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentAdminService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final UploadService uploadService; // ✅ add this
    private final CategoryRepository categoryRepository;

    @Transactional
    public ContentResponse create(CreateContentRequest req, AuthUser admin) {
        var user = userRepository.findById(admin.getId()).orElseThrow();

        String base = toSlug((req.slug() == null || req.slug().isBlank()) ? req.title() : req.slug());
        String slug = ensureUniqueSlug(base, null);

        Content content = Content.builder()
                .title(req.title())
                .body(req.body())
                .type(req.type())
                .status(ContentStatus.DRAFT)
                .slug(slug)
                .excerpt(req.excerpt())
                .coverImageUrl(req.coverImageUrl())
                .coverImagePublicId(req.coverImagePublicId())
                .createdBy(user)
                .build();

        content = contentRepository.save(content);
        return toResponse(content);
    }

    @Transactional
    public ContentResponse update(UUID id, UpdateContentRequest req) {
        var content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        content.setTitle(req.title());
        content.setBody(req.body());
        content.setType(req.type());
        content.setStatus(req.status());
        content.setExcerpt(req.excerpt());

        // ✅ handle cover image update safely
        String newPublicId = req.coverImagePublicId();
        String newUrl = req.coverImageUrl();

        if (newPublicId != null && !newPublicId.isBlank()
                && !newPublicId.equals(content.getCoverImagePublicId())) {

            // delete old image first
            uploadService.deleteImageByPublicId(content.getCoverImagePublicId());

            // set new image
            content.setCoverImagePublicId(newPublicId);
            content.setCoverImageUrl(newUrl);
        } else if ((newPublicId == null || newPublicId.isBlank())) {
            content.setCoverImageUrl(content.getCoverImageUrl());
        }

        // only change slug if provided
        if (req.slug() != null && !req.slug().isBlank()) {
            String base = toSlug(req.slug());
            String newSlug = ensureUniqueSlug(base, content.getId());
            content.setSlug(newSlug);
        }

        return toResponse(content);
    }

    @Transactional
    public void delete(UUID id) {
        var content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        // ✅ delete cloudinary image too
        uploadService.deleteImageByPublicId(content.getCoverImagePublicId());

        contentRepository.delete(content);
    }

    private ContentResponse toResponse(Content c) {
        return new ContentResponse(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getExcerpt(),
                c.getCoverImageUrl(),
                c.getCoverImagePublicId(),
                c.getBody(),
                c.getType(),
                c.getStatus(),
                c.getCreatedBy().getId(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                c.getCategories()
                        .stream()
                        .map(cat -> new CategorySummary(cat.getId(), cat.getName()))
                        .toList());
    }

    private String ensureUniqueSlug(String base, UUID currentContentId) {
        String slug = base;

        // if updating and slug unchanged, allow it
        if (currentContentId != null) {
            var existing = contentRepository.findById(currentContentId).orElse(null);
            if (existing != null && slug.equals(existing.getSlug())) {
                return slug;
            }
        }

        int tries = 0;
        while (contentRepository.existsBySlug(slug)) {
            tries++;
            slug = base + "-" + UUID.randomUUID().toString().substring(0, 8);
            if (tries > 10) {
                throw new RuntimeException("Unable to generate unique slug");
            }
        }
        return slug;
    }

    private String toSlug(String input) {
        String s = (input == null) ? "" : input.trim().toLowerCase();
        s = s.replaceAll("[^a-z0-9]+", "-");
        s = s.replaceAll("(^-+|-+$)", "");
        return s.isBlank() ? "content" : s;
    }

    @Transactional
    public ContentResponse togglePublish(UUID id) {
        var content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        if (content.getStatus() == ContentStatus.PUBLISHED) {
            content.setStatus(ContentStatus.UNPUBLISHED);
        } else {
            content.setStatus(ContentStatus.PUBLISHED);
        }

        return toResponse(content);
    }

    @Transactional
    public ContentResponse assignCategories(UUID contentId, AssignCategoriesRequest req) {
        var content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        var categories = categoryRepository.findAllById(req.categoryIds());
        if (categories.size() != req.categoryIds().size()) {
            throw new IllegalArgumentException("One or more categories not found");
        }

        content.getCategories().clear();
        content.getCategories().addAll(categories);

        return toResponse(content);
    }

}

// Isa Jibril
