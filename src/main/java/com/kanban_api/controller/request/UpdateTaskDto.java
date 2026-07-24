package com.kanban_api.controller.request;

import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskDto(

    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    String title,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotNull(message = "Status is mandatory")
    Status status,

    @NotNull(message = "Priority is mandatory")
    Priority priority,

    @NotNull(message = "Version is mandatory")
    Long version

) {

}
