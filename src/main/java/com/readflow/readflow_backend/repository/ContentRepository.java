package com.readflow.readflow_backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readflow.readflow_backend.entity.Content;

public interface ContentRepository extends JpaRepository<Content, UUID> {
    boolean existsBySlug(String slug);
}
