package com.readflow.readflow_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.admin.CategoryResponse;
import com.readflow.readflow_backend.dto.admin.CreateCategoryRequest;
import com.readflow.readflow_backend.entity.Category;
import com.readflow.readflow_backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CreateCategoryRequest req) {
        if (categoryRepository.existsByNameIgnoreCase(req.name())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category c = Category.builder()
                .name(req.name().trim())
                .build();

        c = categoryRepository.save(c);

        return new CategoryResponse(c.getId(), c.getName());
    }
}
