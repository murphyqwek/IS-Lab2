package com.example.is.exception;

public record ApiErrorResponse(
        int status,
        String message
) {
}
