package com.example.demotestmaven.exception;

import java.util.IllegalFormatException;

import org.springframework.http.HttpStatus;

public enum ApiErrorType {
    //User error
    USER_NOT_FOUND("USER_404", "User not found with username: %s", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_400", "User already exists", HttpStatus.BAD_REQUEST),
    USER_INVALID_INPUT("USER_400", "Invalid input", HttpStatus.BAD_REQUEST),
    USER_INVALID_USERNAME("USER_400", "Invalid username", HttpStatus.BAD_REQUEST),
    USER_INVALID_PASSWORD("USER_400", "Invalid password", HttpStatus.BAD_REQUEST),
    USER_INVALID_EMAIL("USER_400", "Invalid email", HttpStatus.BAD_REQUEST),
    USER_EMAIL_ALREADY_EXISTS("USER_400", "Email already exists", HttpStatus.BAD_REQUEST),
    USER_ROLE_INVALID("USER_400", "Invalid role", HttpStatus.BAD_REQUEST),
    USER_ROLE_TYPE_INVALID("USER_400", "Invalid role type", HttpStatus.BAD_REQUEST),
    USER_ROLE_CODE_INVALID("USER_400", "Invalid role code", HttpStatus.BAD_REQUEST),
    USER_ROLE_DUPLICATE("USER_400", "Duplicate role code: %s. A user cannot have the same role multiple times.", HttpStatus.BAD_REQUEST),


    //Role error
    ROLE_NOT_FOUND("ROLE_404", "Role not found with name: %s", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS("ROLE_400", "Role already exists", HttpStatus.BAD_REQUEST),
    ROLE_INVALID_INPUT("ROLE_400", "Invalid input", HttpStatus.BAD_REQUEST),
    ROLE_REQUIRED_FIELD_MISSING("ROLE_400", "Required role for user %s is missing", HttpStatus.BAD_REQUEST),
    //Authorization error
    UNAUTHORIZED_ACCESS("AUTH_401", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_OPERATION("AUTH_403", "Forbidden operation, only admin can access", HttpStatus.FORBIDDEN),

    //Internal server error
    INTERNAL_SERVER_ERROR("INTERNAL_500", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    ;


    private final String code;
    private final String message;
    private final HttpStatus status;

    ApiErrorType(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }   

    public String getCode() {
        return code;
    }   

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
     public String getFormattedMessage(Object... args) {        
        try {
            return String.format(message, args);
        } catch (IllegalFormatException e) {
            return message; // fallback if formatting fails
        }
    }
}