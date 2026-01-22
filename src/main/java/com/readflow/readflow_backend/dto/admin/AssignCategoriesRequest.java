package com.readflow.readflow_backend.dto.admin;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;

public record AssignCategoriesRequest(
                @NotEmpty List<UUID> categoryIds) {
}
