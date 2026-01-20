package com.readflow.readflow_backend.controller.content;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.content.ContentDetailResponse;
import com.readflow.readflow_backend.dto.content.ContentPublicResponse;
import com.readflow.readflow_backend.security.AuthUser;
import com.readflow.readflow_backend.service.ContentReadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentController {

    private final ContentReadService contentReadService;

    @GetMapping("/free")
    public Page<ContentPublicResponse> free(Pageable pageable) {
        return contentReadService.listFree(pageable);
    }

    @GetMapping("/premium")
    public Page<ContentPublicResponse> premium(
            Authentication authentication,
            Pageable pageable) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        return contentReadService.listPremium(user, pageable);
    }

    @GetMapping("/{id}")
    public ContentDetailResponse single(@PathVariable UUID id,
            Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();
        return contentReadService.viewSingle(id, user);
    }

}
