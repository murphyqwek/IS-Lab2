package com.example.is.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice
public class GlobalConcurrentExceptionHandler {
    @ExceptionHandler({
            DataAccessException.class,
            TransactionSystemException.class
    })
    public ResponseEntity<Map<String, String>> handleDatabaseException(RuntimeException exception) {
        if (isSerializationFailure(exception)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "Данные были изменены другим пользователем. Обновите страницу и повторите редактирование"
                    ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message",
                        "Не удалось выполнить операцию с базой данных"
                ));
    }

    private boolean isSerializationFailure(Throwable exception) {
        Throwable current = exception;

        while (current != null) {
            if (current instanceof SQLException sqlException) {
                for (SQLException sql = sqlException;
                     sql != null;
                     sql = sql.getNextException()) {

                    if ("40001".equals(sql.getSQLState())) {
                        return true;
                    }
                }
            }

            current = current.getCause();
        }

        return false;
    }
}
