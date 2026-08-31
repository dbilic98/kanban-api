package com.kanban_api.domain.dto;

import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;

public record PatchTaskDto(

    String title,

    String description,

    Status status,

    Priority priority
) {

}
