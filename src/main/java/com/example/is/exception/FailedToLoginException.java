package com.example.is.exception;

public class FailedToLoginException extends RuntimeException {
    public FailedToLoginException(String message) {
        super(message);
    }

    public FailedToLoginException() { super(); }
}
