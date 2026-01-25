package com.readflow.readflow_backend.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.bookmark.BookmarkResponse;
import com.readflow.readflow_backend.dto.bookmark.CreateBookmarkRequest;
import com.readflow.readflow_backend.entity.Bookmark;
import com.readflow.readflow_backend.repository.BookmarkRepository;
import com.readflow.readflow_backend.repository.ContentRepository;
import com.readflow.readflow_backend.repository.UserRepository;
import com.readflow.readflow_backend.security.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookmarkResponse create(CreateBookmarkRequest req, AuthUser user) {
        UUID userId = user.getId();
        UUID contentId = req.contentId();

        if (bookmarkRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new IllegalArgumentException("Already bookmarked");
        }

        var content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        var u = userRepository.findById(userId).orElseThrow();

        Bookmark bm = Bookmark.builder()
                .user(u)
                .content(content)
                .build();

        bm = bookmarkRepository.save(bm);
        return toResponse(bm);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> list(AuthUser user, Pageable pageable) {
        return bookmarkRepository.findByUserId(user.getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public void delete(UUID bookmarkId, AuthUser user) {
        var bm = bookmarkRepository.findByIdAndUserId(bookmarkId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Bookmark not found"));
        bookmarkRepository.delete(bm);
    }

    private BookmarkResponse toResponse(Bookmark bm) {
        var c = bm.getContent();
        return new BookmarkResponse(
                bm.getId(),
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getExcerpt(),
                c.getCoverImageUrl(),
                bm.getCreatedAt());
    }
}
