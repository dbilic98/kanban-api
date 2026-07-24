package com.kanban_api.controller.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PaginatedResponse<T> {

  private final long totalItems;
  private final int totalPages;
  private final int currentPage;
  private final int pageSize;
  private final Iterable<T> items;

  public PaginatedResponse(Page<T> page) {
    this.totalItems = page.getTotalElements();
    this.totalPages = page.getTotalPages();
    this.currentPage = page.getNumber();
    this.pageSize = page.getSize();
    this.items = page.getContent();
  }
}
