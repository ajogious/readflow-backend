package com.readflow.readflow_backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.readflow.readflow_backend.entity.*;

public interface ContentRepository extends JpaRepository<Content, UUID> {
    boolean existsBySlug(String slug);

    Page<Content> findByStatusAndType(ContentStatus status, ContentType type, Pageable pageable);
}
