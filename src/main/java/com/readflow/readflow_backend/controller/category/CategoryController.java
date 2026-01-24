package com.readflow.readflow_backend.controller.category;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.readflow.readflow_backend.dto.content.CategoryPublicResponse;
import com.readflow.readflow_backend.dto.content.ContentPublicResponse;
import com.readflow.readflow_backend.service.UserCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final UserCategoryService userCategoryService;

    @GetMapping
    public Page<CategoryPublicResponse> list(Pageable pageable) {
        return userCategoryService.listCategories(pageable);
    }

    @GetMapping("/{id}/contents")
    public Page<ContentPublicResponse> contents(@PathVariable UUID id, Pageable pageable, String q) {
        return userCategoryService.listCategoryContents(id, pageable, q);
    }
}
