package com.kanban_api.exception;

public class UpdateTaskOptimisticLockException extends RuntimeException {

  public UpdateTaskOptimisticLockException(String message, Throwable cause) {
    super(message, cause);
  }
}
