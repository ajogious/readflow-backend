package com.readflow.readflow_backend.controller.bookmark;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.bookmark.BookmarkResponse;
import com.readflow.readflow_backend.dto.bookmark.CreateBookmarkRequest;
import com.readflow.readflow_backend.security.AuthUser;
import com.readflow.readflow_backend.service.BookmarkService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public BookmarkResponse create(
            @Valid @RequestBody CreateBookmarkRequest req,
            Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        return bookmarkService.create(req, user);
    }

    @GetMapping
    public Page<BookmarkResponse> list(Authentication authentication, Pageable pageable) {
        AuthUser user = (AuthUser) authentication.getPrincipal();
        return bookmarkService.list(user, pageable);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        bookmarkService.delete(id, user);
    }
}
