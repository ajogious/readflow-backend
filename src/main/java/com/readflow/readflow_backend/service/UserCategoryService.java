package com.readflow.readflow_backend.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readflow.readflow_backend.dto.content.CategoryPublicResponse;
import com.readflow.readflow_backend.dto.content.ContentPublicResponse;
import com.readflow.readflow_backend.repository.CategoryRepository;
import com.readflow.readflow_backend.repository.ContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final CategoryRepository categoryRepository;
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public Page<CategoryPublicResponse> listCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(c -> new CategoryPublicResponse(c.getId(), c.getName()));
    }

    @Transactional(readOnly = true)
    public Page<ContentPublicResponse> listCategoryContents(UUID categoryId, Pageable pageable, String q) {
        // validate category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found");
        }

        return contentRepository.findPublishedByCategory(categoryId, q, pageable);
    }
}
