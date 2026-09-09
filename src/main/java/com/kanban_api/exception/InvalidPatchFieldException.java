package com.kanban_api.exception;

public class InvalidPatchFieldException extends RuntimeException {

  public InvalidPatchFieldException(String message) {
    super(message);
  }
}
