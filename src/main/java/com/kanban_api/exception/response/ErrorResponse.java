package com.kanban_api.exception.response;

public record ErrorResponse(
    int httpStatusCode,
    String error,
    int errorCode,
    String message,
    String path
) {

}
