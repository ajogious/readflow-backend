package com.readflow.readflow_backend.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.review.CreateReviewRequest;
import com.readflow.readflow_backend.dto.review.ReviewResponse;
import com.readflow.readflow_backend.entity.ContentStatus;
import com.readflow.readflow_backend.entity.ContentType;
import com.readflow.readflow_backend.entity.Review;
import com.readflow.readflow_backend.repository.ContentRepository;
import com.readflow.readflow_backend.repository.ReviewRepository;
import com.readflow.readflow_backend.repository.UserRepository;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Transactional
    public ReviewResponse create(CreateReviewRequest req, AuthUser authUser) {
        UUID userId = authUser.getId();

        var content = contentRepository.findById(req.contentId())
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        // only allow review for published content
        if (content.getStatus() != ContentStatus.PUBLISHED) {
            throw new IllegalArgumentException("Content not available for review");
        }

        // premium: only subscribed users can review (recommended)
        if (content.getType() == ContentType.PREMIUM
                && !subscriptionService.isSubscriptionActive(userId)) {
            throw new SecurityException("Active subscription required to review premium content");
        }

        if (reviewRepository.existsByUserIdAndContentId(userId, content.getId())) {
            throw new IllegalArgumentException("You already reviewed this content");
        }

        var user = userRepository.findById(userId).orElseThrow();

        Review review = Review.builder()
                .user(user)
                .content(content)
                .rating(req.rating())
                .comment(req.comment())
                .build();

        review = reviewRepository.save(review);
        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> listForContent(UUID contentId, Pageable pageable) {
        // content must exist (and be published if you want)
        var content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        if (content.getStatus() != ContentStatus.PUBLISHED) {
            throw new IllegalArgumentException("Content not available");
        }

        return reviewRepository.findByContentId(contentId, pageable)
                .map(this::toResponse);
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getUser().getId(),
                r.getContent().getId(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt());
    }
}
