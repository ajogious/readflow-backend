package com.readflow.readflow_backend.controller.content;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.review.CreateReviewRequest;
import com.readflow.readflow_backend.dto.review.ReviewResponse;
import com.readflow.readflow_backend.security.AuthUser;
import com.readflow.readflow_backend.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody CreateReviewRequest req,
            @AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(reviewService.create(req, user));
    }
}
