package com.readflow.readflow_backend.controller.progress;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.progress.ProgressResponse;
import com.readflow.readflow_backend.dto.progress.SaveProgressRequest;
import com.readflow.readflow_backend.security.AuthUser;
import com.readflow.readflow_backend.service.ReadingProgressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/progress")
public class ReadingProgressController {

    private final ReadingProgressService progressService;

    @PostMapping
    public ProgressResponse save(@Valid @RequestBody SaveProgressRequest req, Authentication authentication) {
        AuthUser user = (AuthUser) authentication.getPrincipal();
        return progressService.save(req, user);
    }

    @GetMapping("/{contentId}")
    public ProgressResponse get(@PathVariable UUID contentId, Authentication authentication) {
        AuthUser user = (AuthUser) authentication.getPrincipal();
        return progressService.get(contentId, user);
    }
}
