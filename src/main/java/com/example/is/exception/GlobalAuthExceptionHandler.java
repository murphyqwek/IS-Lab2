package com.example.is.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalAuthExceptionHandler {
    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserAlreadyExistException(UserAlreadyExistException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Имя пользователя уже занято");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleLoginError() {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Неверное имя пользователя или пароль"));
    }
}
