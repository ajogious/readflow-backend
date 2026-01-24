package com.readflow.readflow_backend.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.readflow.readflow_backend.dto.content.ContentPublicResponse;
import com.readflow.readflow_backend.entity.*;

public interface ContentRepository extends JpaRepository<Content, UUID> {

    boolean existsBySlug(String slug);

    Page<Content> findByStatusAndType(ContentStatus status, ContentType type, Pageable pageable);

    @Query("""
                select new com.readflow.readflow_backend.dto.content.ContentPublicResponse(
                    c.id, c.title, c.slug, c.excerpt, c.coverImageUrl, c.type, c.status, c.createdAt, c.updatedAt
                )
                from Content c
                join c.categories cat
                where cat.id = :categoryId
                  and c.status = com.readflow.readflow_backend.entity.ContentStatus.PUBLISHED
                  and (
                      :q is null or :q = '' or
                      lower(c.title) like lower(concat('%', :q, '%')) or
                      lower(c.slug)  like lower(concat('%', :q, '%'))
                  )
            """)
    Page<ContentPublicResponse> findPublishedByCategory(UUID categoryId, String q, Pageable pageable);

    @Query("""
                select c from Content c
                where c.status = com.readflow.readflow_backend.entity.ContentStatus.PUBLISHED
                  and c.type = com.readflow.readflow_backend.entity.ContentType.FREE
                  and (
                      :q is null or :q = '' or
                      lower(c.title) like lower(concat('%', :q, '%')) or
                      lower(c.slug)  like lower(concat('%', :q, '%'))
                  )
            """)
    Page<Content> searchFreePublished(String q, Pageable pageable);

    @Query("""
                select c from Content c
                where c.status = com.readflow.readflow_backend.entity.ContentStatus.PUBLISHED
                  and c.type = com.readflow.readflow_backend.entity.ContentType.PREMIUM
                  and (
                      :q is null or :q = '' or
                      lower(c.title) like lower(concat('%', :q, '%')) or
                      lower(c.slug)  like lower(concat('%', :q, '%'))
                  )
            """)
    Page<Content> searchPremiumPublished(String q, Pageable pageable);
}
