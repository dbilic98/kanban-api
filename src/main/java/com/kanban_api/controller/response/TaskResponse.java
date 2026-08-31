package com.kanban_api.controller.response;

import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;

public record TaskResponse(

    Long id,

    String title,

    String description,

    Status status,

    Priority priority
) {

}
