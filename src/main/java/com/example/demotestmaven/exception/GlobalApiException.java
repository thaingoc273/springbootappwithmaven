package com.example.demotestmaven.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalApiException {
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        ApiErrorType errorType = ex.getErrorType();
        Map<String, Object> error = new HashMap<>();
        error.put("code", errorType.getCode());
        error.put("message", errorType.getFormattedMessage(ex.getArgs()));
        error.put("status", errorType.getStatus());
        error.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(error, errorType.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", "INTERNAL_SERVER_ERROR");
        error.put("message", "An unexpected error occurred");
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        error.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
