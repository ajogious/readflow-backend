package com.readflow.readflow_backend.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.readflow.readflow_backend.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);

    Page<Review> findByContentId(UUID contentId, Pageable pageable);
}
