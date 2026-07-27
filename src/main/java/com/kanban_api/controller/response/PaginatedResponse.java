package com.kanban_api.controller.response;

import org.springframework.data.domain.Page;

public record PaginatedResponse<T>(

    long totalItems,
    int totalPages,
    int currentPage,
    int pageSize,
    Iterable<T> items
) {

  public PaginatedResponse(Page<T> page) {
    this(
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.getSize(),
        page.getContent()
    );
  }
}
