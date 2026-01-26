package com.readflow.readflow_backend.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.progress.ProgressResponse;
import com.readflow.readflow_backend.dto.progress.SaveProgressRequest;
import com.readflow.readflow_backend.entity.ReadingProgress;
import com.readflow.readflow_backend.repository.ContentRepository;
import com.readflow.readflow_backend.repository.ReadingProgressRepository;
import com.readflow.readflow_backend.repository.UserRepository;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadingProgressService {

    private final ReadingProgressRepository progressRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProgressResponse save(SaveProgressRequest req, AuthUser user) {
        UUID userId = user.getId();
        UUID contentId = req.contentId();

        var content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        // Upsert
        ReadingProgress progress = progressRepository
                .findByUserIdAndContentId(userId, contentId)
                .orElseGet(() -> ReadingProgress.builder()
                        .user(userRepository.findById(userId).orElseThrow())
                        .content(content)
                        .position(0)
                        .percent(0.0)
                        .build());

        progress.setPosition(req.position());
        progress.setPercent(req.percent());

        progress = progressRepository.save(progress);

        // BaseEntity updatedAt should update, but return safely:
        Instant updated = progress.getUpdatedAt() != null ? progress.getUpdatedAt() : Instant.now();

        return new ProgressResponse(contentId, progress.getPosition(), progress.getPercent(), updated);
    }

    @Transactional(readOnly = true)
    public ProgressResponse get(UUID contentId, AuthUser user) {
        var progress = progressRepository.findByUserIdAndContentId(user.getId(), contentId)
                .orElseThrow(() -> new IllegalArgumentException("Progress not found"));

        Instant updated = progress.getUpdatedAt() != null ? progress.getUpdatedAt() : Instant.now();
        return new ProgressResponse(contentId, progress.getPosition(), progress.getPercent(), updated);
    }
}
