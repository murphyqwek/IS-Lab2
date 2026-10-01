package com.example.lab1.exception;

public record ApiErrorResponse(
        int status,
        String message
) {
}
