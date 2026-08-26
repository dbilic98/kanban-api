package com.kanban_api.exception;

import com.kanban_api.exception.response.ErrorCodes;
import com.kanban_api.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFoundException(
      TaskNotFoundException e,
      HttpServletRequest request) {

    log.warn("Task not found exception, message: {}", e.getMessage());

    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        "Task error",
        ErrorCodes.TASK_NOT_FOUND.getCode(),
        e.getMessage(),
        request
    );
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status,
      String errorType,
      int errorCode,
      String message,
      HttpServletRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
        status.value(),
        errorType,
        errorCode,
        message,
        request.getRequestURI()
    );

    return ResponseEntity.status(status).body(errorResponse);
  }
}
