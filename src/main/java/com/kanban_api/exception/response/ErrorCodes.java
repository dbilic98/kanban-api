package com.kanban_api.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {

  //Task error 21xx
  TASK_NOT_FOUND(2100, "Task not found"),

  //Validation 40xx
  VALIDATION_ERROR(4001, "Validation error"),
  REQUIRED_FIELD_MISSING(4002, "Required field missing"),
  INVALID_FORM(4003, "Invalid format"),

  //Serve 50xx
  INTERNAL_ERROR(5001, "Internal server error");

  private final int code;
  private final String message;
}
