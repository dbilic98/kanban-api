package com.kanban_api.exception;

public class TaskOptimisticLockException extends RuntimeException {

  public TaskOptimisticLockException(String message) {
    super(message);
  }
}
