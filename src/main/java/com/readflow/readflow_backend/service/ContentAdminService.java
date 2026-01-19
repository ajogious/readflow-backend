package com.readflow.readflow_backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.admin.*;
import com.readflow.readflow_backend.entity.Content;
import com.readflow.readflow_backend.entity.ContentStatus;
import com.readflow.readflow_backend.repository.*;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentAdminService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ContentResponse create(CreateContentRequest req, AuthUser admin) {
        var user = userRepository.findById(admin.getId()).orElseThrow();

        Content content = Content.builder()
                .title(req.title())
                .body(req.body())
                .type(req.type())
                .status(ContentStatus.DRAFT)
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

        return toResponse(content);
    }

    @Transactional
    public void delete(UUID id) {
        if (!contentRepository.existsById(id)) {
            throw new IllegalArgumentException("Content not found");
        }
        contentRepository.deleteById(id);
    }

    private ContentResponse toResponse(Content c) {
        return new ContentResponse(
                c.getId(),
                c.getTitle(),
                c.getBody(),
                c.getType(),
                c.getStatus(),
                c.getCreatedBy().getId(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
