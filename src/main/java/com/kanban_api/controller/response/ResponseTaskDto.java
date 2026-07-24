package com.kanban_api.controller.response;

import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;

public class ResponseTaskDto {

  private Long id;

  private String title;

  private String description;

  private Status status;

  private Priority priority;

  private Long version;
}
