package com.readflow.readflow_backend.controller.admin;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.admin.*;
import com.readflow.readflow_backend.security.AuthUser;
import com.readflow.readflow_backend.service.ContentAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/contents")
@RequiredArgsConstructor
@Validated
public class AdminContentController {

    private final ContentAdminService contentAdminService;

    @PostMapping
    public ResponseEntity<ContentResponse> create(
            @AuthenticationPrincipal AuthUser admin,
            @Valid @RequestBody CreateContentRequest req) {
        return ResponseEntity.ok(contentAdminService.create(req, admin));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateContentRequest req) {
        return ResponseEntity.ok(contentAdminService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        contentAdminService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/publish")
    public ContentResponse togglePublish(@PathVariable UUID id) {
        return contentAdminService.togglePublish(id);
    }

    @PostMapping("/{id}/categories")
    public ContentResponse assignCategories(@PathVariable UUID id,
            @RequestBody @Valid AssignCategoriesRequest req) {
        return contentAdminService.assignCategories(id, req);
    }

}
