package com.readflow.readflow_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.readflow.readflow_backend.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);

    Optional<Bookmark> findByIdAndUserId(UUID id, UUID userId);

    Page<Bookmark> findByUserId(UUID userId, Pageable pageable);
}