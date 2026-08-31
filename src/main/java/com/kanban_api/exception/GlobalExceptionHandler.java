package com.kanban_api.exception;

import com.kanban_api.exception.response.ErrorCodes;
import com.kanban_api.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFoundException(
      TaskNotFoundException e,
      HttpServletRequest request) {

    log.warn("Task not found exception, message: {}", e.getMessage());

    return buildErrorResponse(
        HttpStatus.NOT_FOUND,
        "Task error",
        ErrorCodes.TASK_NOT_FOUND,
        e.getMessage(),
        request
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e,
      HttpServletRequest request) {

    String message = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining(" , "));

    log.warn("Validation error: {}", message);

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Validation error",
        ErrorCodes.VALIDATION_ERROR,
        message,
        request
    );
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
      MissingServletRequestParameterException e,
      HttpServletRequest request) {

    log.warn("Required request parameter missing: {}", e.getParameterName());

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Validation error",
        ErrorCodes.REQUIRED_FIELD_MISSING,
        "Required parameter missing: " + e.getParameterName(),
        request
    );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException e,
      HttpServletRequest request) {

    log.warn("Invalid parameter '{}', value: {}", e.getName(), e.getValue());

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Validation error",
        ErrorCodes.INVALID_FORM,
        "Invalid value for parameter: " + e.getName(),
        request
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidForm(
      HttpMessageNotReadableException e,
      HttpServletRequest request) {

    log.warn("Invalid request body: {}", e.getMessage());

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST,
        "Validation error",
        ErrorCodes.INVALID_FORM,
        "Request body has invalid format",
        request
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(
      Exception e,
      HttpServletRequest request) {

    log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), e);

    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Server error",
        ErrorCodes.INTERNAL_ERROR,
        ErrorCodes.INVALID_FORM.getMessage(),
        request
    );
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status,
      String errorType,
      ErrorCodes errorCode,
      String message,
      HttpServletRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
        status.value(),
        errorType,
        errorCode.getCode(),
        message,
        request.getRequestURI()
    );

    return ResponseEntity.status(status).body(errorResponse);
  }
}
