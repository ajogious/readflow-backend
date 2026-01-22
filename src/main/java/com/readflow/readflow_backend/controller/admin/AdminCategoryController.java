package com.readflow.readflow_backend.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.admin.CategoryResponse;
import com.readflow.readflow_backend.dto.admin.CreateCategoryRequest;
import com.readflow.readflow_backend.service.AdminCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest req) {
        return ResponseEntity.ok(adminCategoryService.create(req));
    }
}
