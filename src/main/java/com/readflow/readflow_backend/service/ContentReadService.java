package com.readflow.readflow_backend.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.content.ContentDetailResponse;
import com.readflow.readflow_backend.dto.content.ContentPublicResponse;
import com.readflow.readflow_backend.entity.*;
import com.readflow.readflow_backend.repository.ContentRepository;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentReadService {

    private final ContentRepository contentRepository;
    private final SubscriptionService subscriptionService; // you can stub for now

    @Transactional(readOnly = true)
    public Page<ContentPublicResponse> listFree(Pageable pageable) {
        return contentRepository
                .findByStatusAndType(ContentStatus.PUBLISHED, ContentType.FREE, pageable)
                .map(this::toPublic);
    }

    @Transactional(readOnly = true)
    public Page<ContentPublicResponse> listPremium(AuthUser user, Pageable pageable) {
        if (!subscriptionService.isSubscriptionActive(user.getId())) {
            throw new SecurityException("Active subscription required");
        }

        return contentRepository
                .findByStatusAndType(ContentStatus.PUBLISHED, ContentType.PREMIUM, pageable)
                .map(this::toPublic);
    }

    @Transactional(readOnly = true)
    public ContentDetailResponse viewSingle(UUID id, AuthUser user) {
        var content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        if (content.getStatus() != ContentStatus.PUBLISHED) {
            throw new SecurityException("Content not available");
        }

        if (content.getType() == ContentType.PREMIUM
                && !subscriptionService.isSubscriptionActive(user.getId())) {
            throw new SecurityException("Active subscription required");
        }

        return toDetail(content);
    }

    private ContentDetailResponse toDetail(Content c) {
        return new ContentDetailResponse(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getExcerpt(),
                c.getCoverImageUrl(),
                c.getBody(),
                c.getType(),
                c.getStatus(),
                c.getCreatedBy().getId(), // ✅ safe, just UUID
                c.getCreatedAt(),
                c.getUpdatedAt());
    }

    private ContentPublicResponse toPublic(Content c) {
        return new ContentPublicResponse(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getExcerpt(),
                c.getCoverImageUrl(),
                c.getType(),
                c.getStatus(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
