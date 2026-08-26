package com.kanban_api.domain.dto;

import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatchTaskDto(
    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    String title,

    String description,

    Status status,

    Priority priority
) {

}
