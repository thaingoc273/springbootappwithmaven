package com.example.demotestmaven.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", getHttpStatus(ex.getErrorCode()).value());
        body.put("error", ex.getErrorCode().name());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, getHttpStatus(ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "INTERNAL_SERVER_ERROR");
        body.put("message", "An unexpected error occurred");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        switch (errorCode) {
            case UNAUTHORIZED_ACCESS:
                return HttpStatus.UNAUTHORIZED;
            case FORBIDDEN_OPERATION:
                return HttpStatus.FORBIDDEN;
            case RESOURCE_NOT_FOUND:
            case USER_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case DUPLICATE_RESOURCE:
            case INVALID_INPUT:
            case INVALID_USERNAME:
            case INVALID_PASSWORD:
            case INVALID_EMAIL:
            case INVALID_DATE_FORMAT:
            case INVALID_ROLE:
                return HttpStatus.BAD_REQUEST;
            case OPERATION_FAILED:
            case DATA_INTEGRITY_VIOLATION:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
} 